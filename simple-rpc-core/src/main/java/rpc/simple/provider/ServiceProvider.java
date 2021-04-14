package rpc.simple.provider;
import rpc.simple.model.Service;

/**
 * @author zeng.fk
 *     2021-04-05 00:39
 */
public interface ServiceProvider {

    void publish(Object service, String serviceName, String version, String tagName);

    Object get(Service service);

}
