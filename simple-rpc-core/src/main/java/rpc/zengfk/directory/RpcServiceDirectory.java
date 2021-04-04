package rpc.zengfk.directory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.model.ServiceInstance;

/**
 * 服务实例缓存, 调用时优先从缓存中返回, 缓存中没有在去注册中心拉取
 * 也会在注册中心回调时刷新
 * @author zeng.fk
 *     2021-04-01 23:07
 */
@Data
@Slf4j
public class RpcServiceDirectory {

    /**
     * key: 服务名称
     * value: 服务实例列表
     */
    private static final Map<String, List<ServiceInstance>> CACHE_MAP = Maps.newConcurrentMap();

    public static void add(String serviceName, ServiceInstance serviceInstance) {

        List<ServiceInstance> serviceInstances = CACHE_MAP.get(serviceName);
        if (serviceInstances == null) {
            serviceInstances = Lists.newArrayList();
        }
        if (serviceInstances.contains(serviceInstance)) {
            log.warn("服务实例已存在，serviceInstance：" + serviceInstance);
        }
        serviceInstances.add(serviceInstance);
    }

    public static List<ServiceInstance> get(String serviceName) {

        return Optional.ofNullable(CACHE_MAP.get(serviceName)).orElse(Lists.newArrayList());
    }

    public static void refresh(String serviceName,List<ServiceInstance> cache) {

        List<ServiceInstance> serviceInstances = CACHE_MAP.get(serviceName);
        log.info("服务实例缓存刷新, serviceName:{}, {}条 -> {} 条", serviceName, serviceInstances.size(), cache.size());
        CACHE_MAP.put(serviceName, cache);
    }

}
