package rpc.zengfk.provider;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.extension.ExtensionLoader;
import rpc.zengfk.extension.ExtensionName;
import rpc.zengfk.model.Service;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.registry.ServiceRegistry;
import rpc.zengfk.router.tag.model.Tag;

import java.net.InetAddress;
import java.util.Map;

/**
 * @author zeng.fk
 *     2021-04-05 00:47
 */
@Slf4j
@Component
public class SimpleServiceProvider implements ServiceProvider{

    @Value("${rpc.remoting.netty.port:9998}")
    public String PORT;

    private final ServiceRegistry registry;
    /**
     * key: Service, 即服务名+版本号+tag  value: serviceBean
     */
    private static final Map<Service, Object> PUBLISHED_SERVICE = Maps.newConcurrentMap();

    public SimpleServiceProvider() {
        this.registry = ExtensionLoader.ofType(ServiceRegistry.class).getExtension(ExtensionName.REGISTRY);
    }

    @Override
    @SneakyThrows
    public void publish(Object serviceBean, String serviceName, String version, String tagName) {

        Tag tag = new Tag(tagName);
        String host = InetAddress.getLocalHost().getHostAddress();
        Service service = new Service(serviceName, version, tag);
        ServiceInstance serviceInstance = new ServiceInstance(service, host, PORT);

        registry.register(serviceInstance);
        Service serviceKey = new Service(serviceName, version, tag);
        PUBLISHED_SERVICE.put(serviceKey, serviceBean);

        log.info("成功注册服务:{}", serviceInstance);
    }

    @Override
    public Object get(Service service) {
        Object serviceBean = PUBLISHED_SERVICE.get(service);
        if (service == null) {
            throw new RpcException("service not found");
        }
        return serviceBean;
    }

}
