package loadBalance;
import java.util.List;
import model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-04-01 16:43
 */
public interface LoadBalance {

    ServiceInstance select(List<ServiceInstance> services, String key);

}
