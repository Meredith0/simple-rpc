package rpc.simple.loadBalance.balancer;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.loadBalance.AbstractLoadBalance;
import rpc.simple.loadBalance.algorithm.ConsistentHashSelector;
import rpc.simple.model.ServiceInstance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性哈希负载均衡器
 *
 * @author zeng.fk
 *     2021-04-02 22:05
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
    protected ServiceInstance doSelect(List<ServiceInstance> servicePool, String key) {
        log.debug("进入负载均衡...{}", key);
        assert servicePool.size() > 1;

        //list中的服务名称均相同
        String serviceName = servicePool.get(0).getServiceName();

        ConsistentHashSelector<ServiceInstance> selector = selectors.get(serviceName);
        int identityHashCode = System.identityHashCode(key);
        log.debug("identityHashCode:{}", identityHashCode);

        // using the hashcode of list to compute the hash only pay attention to the elements in the list
        if (selector == null || selector.getIdentityHashCode() != identityHashCode) {
            selectors.put(serviceName, new ConsistentHashSelector(servicePool, 160, identityHashCode));
            selector = selectors.get(serviceName);
        }

        return selector.select(key);
    }


}
