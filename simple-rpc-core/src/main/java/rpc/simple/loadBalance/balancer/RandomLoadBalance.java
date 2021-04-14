package rpc.simple.loadBalance.balancer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import rpc.simple.loadBalance.AbstractLoadBalance;
import rpc.simple.model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-04-02 19:50
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected ServiceInstance doSelect(List<ServiceInstance> serviceAddresses, String key) {
        int i = ThreadLocalRandom.current().nextInt(serviceAddresses.size());
        return serviceAddresses.get(i);
    }

}
