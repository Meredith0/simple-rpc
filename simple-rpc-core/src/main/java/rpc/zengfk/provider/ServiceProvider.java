package rpc.zengfk.provider;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-04-05 00:39
 */
public interface ServiceProvider {

    void publish(Object service, String serviceName, String version);

    Object get(ServiceInstance serviceInstance);

}
