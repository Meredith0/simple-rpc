package rpc.zengfk.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.registry.ServiceRegistry;
import rpc.zengfk.utils.ZookeeperUtil;

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
