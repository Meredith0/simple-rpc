package rpc.zengfk.registry.zookeeper;
import rpc.zengfk.directory.RpcServiceDirectory;
import rpc.zengfk.exception.RpcException;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import rpc.zengfk.model.Service;
import rpc.zengfk.model.ServiceInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;
import rpc.zengfk.registry.ServiceDiscovery;
import rpc.zengfk.router.tag.model.Tag;
import rpc.zengfk.utils.ZookeeperUtil;

/**
 * @author zeng.fk
 *     2021-04-01 19:28
 */
public class ZkServiceDiscovery implements ServiceDiscovery {

    /**
     * 从注册中心查询可用的服务
     */
    @Override
    @SneakyThrows
    public List<ServiceInstance> lookup(Service service) {
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();
        List<ServiceInstance> cache = RpcServiceDirectory.get(service);
        ArrayList<ServiceInstance> res = Lists.newArrayList();
        //优先从缓存中返回
        if (!cache.isEmpty()) {
            return cache;
        }

        //没有缓存则去注册中心获取
        String servicePath = ZkServiceRegistry.PATH_PREFIX + service.getServiceName();
        List<String> serviceInstanceStrList = zkClient.getChildren().forPath(servicePath);
        if (CollectionUtils.isEmpty(serviceInstanceStrList)) {
            throw new RpcException("无法获取服务调用地址, serviceName:" + service);
        }

        serviceInstanceStrList.forEach(path->{
            ServiceInstance s = parseServicePath(path);
            //放进缓存
            RpcServiceDirectory.add(service, s);
            res.add(s);
        });

        //注册childrenWatcher
        registerWatcher(service);
        return res;
    }

    /**
     * 注册childWatcher
     */
    private void registerWatcher(Service service) throws Exception {
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();
        // /simple-rpc/provider/xxxService 表示一个服务集群
        String prefix = ZkServiceRegistry.PATH_PREFIX + service.getServiceName();

        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, prefix, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> servicePath = curatorFramework.getChildren().forPath(prefix);
            //替换缓存
            List<ServiceInstance> cache = Lists.newArrayList();
            servicePath.forEach(path -> cache.add(parseServicePath(path)));
            RpcServiceDirectory.refresh(service, cache);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    /**
     *  将servicePath解析为ServiceInstance
     */
    private ServiceInstance parseServicePath(String path) {
        // path: {service}#{version}#{tag}#{host}:{port}
        String[] serviceInstanceStr = path.split(ServiceInstance.SEPARATOR);

        if (serviceInstanceStr.length != 4) {
            throw new RpcException("path 解析异常, path:" + path);
        }
        String[] ipAddress = serviceInstanceStr[3].split(":");

        Tag tag = new Tag(serviceInstanceStr[2]);
        Service service = new Service(serviceInstanceStr[0], serviceInstanceStr[1], tag);
        return new ServiceInstance(service, ipAddress[0], ipAddress[1]);

    }
}
