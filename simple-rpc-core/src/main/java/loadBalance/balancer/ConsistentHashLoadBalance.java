package loadBalance.balancer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import loadBalance.AbstractLoadBalance;
import loadBalance.algorithm.ConsistentHashSelector;
import lombok.extern.slf4j.Slf4j;
import model.ServiceInstance;

/**
 * 一致性哈希负载均衡器
 *
 * @author zeng.fk
 *     2021-04-02 14:05
 */
@Slf4j
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    /**
     * 缓存 selector
     * <p>
     *      key: 服务名称
     *      value: 选择器
     * </p>
     *
     */
    private final ConcurrentHashMap<String, ConsistentHashSelector<ServiceInstance>> selectors = new ConcurrentHashMap<>();

    @Override
    protected ServiceInstance doSelect(List<ServiceInstance> serviceInstances, String key) {
        assert serviceInstances.size() > 1;

        //list中的服务名称均相同
        String serviceName = serviceInstances.get(0).getServiceName();

        ConsistentHashSelector<ServiceInstance> selector = selectors.get(serviceName);
        int identityHashCode = System.identityHashCode(key);
        log.debug("identityHashCode:{}",identityHashCode);

        // using the hashcode of list to compute the hash only pay attention to the elements in the list
        if (selector == null || selector.getIdentityHashCode() != identityHashCode) {
            selectors.put(serviceName,
                new ConsistentHashSelector(serviceInstances, serviceInstances.size(), identityHashCode));
            selector = selectors.get(serviceName);
        }

        return selector.select(key);
    }


}
