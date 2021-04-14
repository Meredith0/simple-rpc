package rpc.simple.loadBalance;
import java.util.List;
import rpc.simple.annotation.SPI;
import rpc.simple.model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-04-01 20:43
 */
@SPI
public interface LoadBalance {

    ServiceInstance select(List<ServiceInstance> services, String key);

}
