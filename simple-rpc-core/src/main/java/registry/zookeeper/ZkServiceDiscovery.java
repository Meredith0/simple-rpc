package registry.zookeeper;
import cache.RpcServiceCache;
import exception.RpcException;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import model.ServiceInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;
import registry.ServiceDiscovery;
import utils.ZookeeperUtil;

/**
 * @author zeng.fk
 *     2021-04-01 16:28
 */
public class ZkServiceDiscovery implements ServiceDiscovery {

    /**
     * 从注册中心查询可用的服务
     *
     * @param serviceName 服务名称
     * @return 服务实例
     */
    @Override
    @SneakyThrows
    public List<ServiceInstance> lookup(String serviceName) {
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();
        List<ServiceInstance> cache = RpcServiceCache.get(serviceName);
        ArrayList<ServiceInstance> res = Lists.newArrayList();
        //优先从缓存中返回
        if (!cache.isEmpty()) {
            return cache;
        }

        //没有缓存则去注册中心获取
        String servicePath = ZkServiceRegistry.PATH_PREFIX + "/" + serviceName;
        List<String> serviceInstanceStrList = zkClient.getChildren().forPath(servicePath);
        if (CollectionUtils.isEmpty(serviceInstanceStrList)) {
            throw new RpcException("无法获取服务调用地址, serviceName:" + serviceName);
        }

        serviceInstanceStrList.forEach(path->{
            ServiceInstance s = parseServicePath(path);
            //放进缓存
            RpcServiceCache.add(serviceName, s);
            res.add(s);
        });

        //注册childrenWatcher
        registerWatcher(serviceName);
        return res;
    }

    /**
     * 注册watcher
     */
    private void registerWatcher(String rpcServiceName) throws Exception {
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();

        String service = ZkServiceRegistry.PATH_PREFIX + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, service, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> servicePath = curatorFramework.getChildren().forPath(service);
            //替换缓存
            List<ServiceInstance> cache = Lists.newArrayList();
            servicePath.forEach(path -> cache.add(parseServicePath(path)));
            RpcServiceCache.refresh(rpcServiceName, cache);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    /**
     *  将servicePath解析为ServiceInstance
     */
    private ServiceInstance parseServicePath(String path) {
        // path: /simple-rpc/provider/{service}#{version}#{host}:{port}
        String[] split = path.split("/");
        if (split.length != 3) {
            throw new RpcException("path 解析异常, path:" + path);
        }
        String[] serviceInstanceStr = split[2].split(ServiceInstance.SEPARATOR);
        if (serviceInstanceStr.length != 3) {
            throw new RpcException("path 解析异常, path:" + path);
        }
        String[] ipAddress = serviceInstanceStr[2].split(":");

        return ServiceInstance.builder()
                              .serviceName(serviceInstanceStr[0])
                              .version(serviceInstanceStr[1])
                              .host(ipAddress[0])
                              .port(ipAddress[1])
                              .build();

    }
}
