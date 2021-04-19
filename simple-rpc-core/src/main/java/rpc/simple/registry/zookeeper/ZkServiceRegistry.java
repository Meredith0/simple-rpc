package rpc.simple.registry.zookeeper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import rpc.simple.exception.RpcException;
import rpc.simple.model.Metadata;
import rpc.simple.model.MetadataPool;
import rpc.simple.model.ServiceInstance;
import rpc.simple.registry.ServiceRegistry;
import rpc.simple.utils.ZookeeperUtil;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zeng.fk
 *     2021-03-30 15:10
 */
@Slf4j
@Component
public class ZkServiceRegistry implements ServiceRegistry {
    public static final String PATH_PREFIX = "/simple-rpc/provider/";
    public static final String METADATA_PREFIX = "/simple-rpc/metadata/";
    public static final String DATACENTER_PREFIX = "datacenter/id";
    public static final String WORKER_PREFIX = "worker/id";
    public static final Integer RETRY_INTERVAL = 5 * 1000;

    //缓存本机已注册的服务，供下线时取消注册
    private static final Set<String> REGISTERED_CACHE = ConcurrentHashMap.newKeySet();

    /**
     * 注册服务
     *  /simple-rpc/provider/{service}#{version}#{host}:{port}
     *  前2段为providerPath, 系持久节点, 后面为临时节点
     */
    @Override
    public void register(ServiceInstance serviceInstance) {

        String providerPath = PATH_PREFIX + serviceInstance.getServiceName();
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();
        //检查服务路径是否存在
        Stat stat = null;
        try {
            stat = zkClient.checkExists().forPath(providerPath);
        } catch (Exception e) {
            log.error("检查服务路径是否存在时出错, {}",e.getMessage());
        }
        //不存在则创建
        if (stat == null) {
            try {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(providerPath);
            } catch (Exception e) {
                log.error("创建服务路径失败, {}",e.getMessage());
            }
        }

        //注册临时节点
        String fullPath = providerPath + "/" + toServicePath(serviceInstance);
        try {
            zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(fullPath);
        } catch (Exception e) {
            throw new RpcException("注册服务失败! " + e.getMessage());
        }
        REGISTERED_CACHE.add(fullPath);

        log.info("服务注册成功, servicePath:{}", fullPath);

    }

    /**
     * 注册元数据
     */
    @Override
    @SneakyThrows
    public Metadata register(Metadata metadata) {
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();
        //注册元数据
        String datacenterIdPath = METADATA_PREFIX + DATACENTER_PREFIX;
        String workerIdPath = METADATA_PREFIX + WORKER_PREFIX;
        checkPath(zkClient,datacenterIdPath);
        checkPath(zkClient,workerIdPath);

        //eg: /simple-rpc/metadata/datacenter/id/1
        List<String> registeredDatacenter = zkClient.getChildren().forPath(datacenterIdPath);
        List<String> registeredWorker = zkClient.getChildren().forPath(workerIdPath);

        Integer datacenterId = MetadataPool.getDatacenterId(registeredDatacenter);
        Integer workerId = MetadataPool.getWorkerId(registeredWorker);

        tryCreateDatacenterId(zkClient, datacenterIdPath, datacenterId, metadata);
        tryCreateWorkerId(zkClient, workerIdPath, workerId, metadata);

        return metadata;
    }

    @SneakyThrows
    private void checkPath(CuratorFramework zkClient, String path) {
        Stat datacenterIdPathStat = zkClient.checkExists().forPath(path);
        if (datacenterIdPathStat == null) {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }
    }

    @SneakyThrows
    private void tryCreateDatacenterId(CuratorFramework zkClient, String path, Integer id, Metadata metadata) {

        if (metadata.getDatacenterId() != null) {
            return;
        }
        try {
            zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(path + "/" + id);
            metadata.setDatacenterId(id);
            Metadata.register(metadata);
        } catch (Exception e) {
            //重试, 如果节点已满, 会在MetadataPool.getFromPool里抛出异常
            log.warn("retry creating path:{}", path);
            Thread.sleep(RETRY_INTERVAL);
            register(metadata);
        }
    }

    @SneakyThrows
    private void tryCreateWorkerId(CuratorFramework zkClient, String path, Integer id, Metadata metadata) {

        if (metadata.getWorkerId() != null) {
            return;
        }
        try {
            zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(path + "/" + id);
            metadata.setWorkerId(id);
            Metadata.register(metadata);
        } catch (Exception e) {
            //重试, 如果节点已满, 会在MetadataPool.getFromPool里抛出异常
            log.warn("retry creating path:{}", path);
            Thread.sleep(RETRY_INTERVAL);
            register(metadata);
        }
    }
    /**
     * 取消注册服务, 优雅停机时调用
     */
    @Override
    public void unRegister() {
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();
        //删除该ip下的所有服务
        REGISTERED_CACHE.forEach(path -> {
            try {
                zkClient.delete().forPath(path);
                REGISTERED_CACHE.remove(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        log.info("服务取消注册成功");
    }

    private String toServicePath(ServiceInstance s) {
        return String.join(ServiceInstance.SEPARATOR, s.getServiceName(), s.getVersion(), s.getTag().getName(),
            s.getHost() + ":" + s.getPort());
    }

}
