package loadBalance;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import model.ServiceInstance;
import org.springframework.util.CollectionUtils;

/**
 * @author zeng.fk
 *     2021-04-01 16:59
 */
@Slf4j
public abstract class AbstractLoadBalance implements LoadBalance {

    /**
     * @param services 服务实例列表
     * @param key 用于计算哈希值, 目前仅用于ConsistentHashLoadBalance
     * @return 服务实例
     */
    @Override
    public ServiceInstance select(List<ServiceInstance> services, String key) {
        if (CollectionUtils.isEmpty(services)) {
            log.warn("负责均衡时查无可用服务列表");
            return null;
        }

        if (services.size() == 1) {
            return services.get(0);
        }

        return doSelect(services, key);
    }

    protected abstract ServiceInstance doSelect(List<ServiceInstance> serviceAddresses, String key);

}
