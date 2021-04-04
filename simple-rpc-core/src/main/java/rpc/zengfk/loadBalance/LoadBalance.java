package rpc.zengfk.loadBalance;
import java.util.List;
import rpc.zengfk.annotation.SPI;
import rpc.zengfk.model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-04-01 16:43
 */
@SPI
public interface LoadBalance {

    ServiceInstance select(List<ServiceInstance> services, String key);

}