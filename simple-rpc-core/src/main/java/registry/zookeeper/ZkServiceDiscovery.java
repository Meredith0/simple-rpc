package registry.zookeeper;
import cache.RegisteredServiceCache;
import exception.RpcException;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import model.ServiceInstance;
import org.apache.curator.framework.CuratorFramework;
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
        List<String> cache = RegisteredServiceCache.CACHE_MAP.get(serviceName);
        ArrayList<ServiceInstance> res = new ArrayList<>();
        //优先从缓存中返回
        if (cache != null && !cache.isEmpty()) {
            cache.forEach(path->{
                res.add(ServiceInstance.parseServicePath(path));
            });
            return res;
        }

        //没有缓存则去注册中心获取
        String servicePath = ZkServiceRegistry.PATH_PREFIX + "/" + serviceName;
        List<String> serviceInstanceStrList = zkClient.getChildren().forPath(servicePath);
        if (serviceInstanceStrList == null || serviceInstanceStrList.isEmpty()) {
            throw new RpcException("无法获取服务调用地址, serviceName:" + serviceName);
        }
        //放进缓存
        RegisteredServiceCache.CACHE_MAP.put(serviceName, serviceInstanceStrList);
        serviceInstanceStrList.forEach(path->{
            res.add(ServiceInstance.parseServicePath(path));
        });
        //注册childrenWatcher
        ZookeeperUtil.registerWatcher(serviceName);
        return res;
    }

}
