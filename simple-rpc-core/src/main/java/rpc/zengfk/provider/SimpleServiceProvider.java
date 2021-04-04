package rpc.zengfk.provider;
import com.google.common.collect.Maps;
import java.net.InetAddress;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rpc.zengfk.annotation.RpcService;
import rpc.zengfk.constant.ExtensionName;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.extension.ExtensionLoader;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.registry.ServiceRegistry;
import rpc.zengfk.remoting.transport.netty.server.NettyRpcServer;

/**
 * @author zeng.fk
 *     2021-04-05 00:47
 */
@Slf4j
@Component
public class SimpleServiceProvider implements ServiceProvider{

    private final ServiceRegistry registry;
    private static final Map<ServiceInstance, Object> PUBLISHED_SERVICE = Maps.newConcurrentMap();

    public SimpleServiceProvider() {
        this.registry = ExtensionLoader.ofType(ServiceRegistry.class).getExtension(ExtensionName.REGISTRY);
    }

    @Override
    @SneakyThrows
    public void publish(Object service, String serviceName, String version) {

        String host = InetAddress.getLocalHost().getHostAddress();
        ServiceInstance serviceInstance = ServiceInstance.builder()
                                                         .serviceName(serviceName)
                                                         .version(version)
                                                         .host(host)
                                                         .port(NettyRpcServer.PORT)
                                                         .build();
        registry.register(serviceInstance);
        PUBLISHED_SERVICE.put(serviceInstance, service);

        log.info("Successfully published service:{}", serviceName);

    }

    @Override
    public Object get(ServiceInstance serviceInstance) {
        Object service = PUBLISHED_SERVICE.get(serviceInstance);
        if (service == null) {
            log.error("service not found, serviceInstance:{}",serviceInstance);
            throw new RpcException("service not found");
        }
        return service;
    }

}
