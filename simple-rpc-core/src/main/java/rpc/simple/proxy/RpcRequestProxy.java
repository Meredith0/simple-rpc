package rpc.simple.proxy;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.exception.RpcException;
import rpc.simple.extension.ExtensionLoader;
import rpc.simple.extension.ExtensionName;
import rpc.simple.filter.FilterCache;
import rpc.simple.filter.FilterChain;
import rpc.simple.filter.lifecycle.ClientBeforeSendFilter;
import rpc.simple.filter.lifecycle.ClientInvokedFilter;
import rpc.simple.model.RpcRequest;
import rpc.simple.model.RpcResponse;
import rpc.simple.model.Service;
import rpc.simple.model.ServiceInstance;
import rpc.simple.registry.ServiceDiscovery;
import rpc.simple.remoting.transport.RpcTransport;
import rpc.simple.router.Router;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 代理客户端, 发起rpc请求
 *
 * @author zeng.fk
 * 2021-04-05 16:11
 */
@Slf4j
public class RpcRequestProxy implements Proxy {

    private final ServiceDiscovery serviceDiscovery;
    private final Router router;
    private final RpcTransport rpcTransport;
    private final Service service;

    public RpcRequestProxy(RpcTransport rpcTransport, Service service) {
        this.serviceDiscovery = ExtensionLoader.ofType(ServiceDiscovery.class).getExtension(ExtensionName.DISCOVERY);
        this.router = ExtensionLoader.ofType(Router.class).getExtension(ExtensionName.ROUTER);
        this.rpcTransport = rpcTransport;
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    public <T> T newProxyInstance(Class<T> clazz) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    private Object doProxy(Method method, Object[] args) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        log.info("发起rpc请求, requestId:{}", requestId);

        //过滤器
        FilterChain clientInvokedFilter = FilterCache.get(ClientInvokedFilter.class);
        clientInvokedFilter.invokeChain(method, args);

        RpcRequest rpcRequest = RpcRequest.builder()
            .requestId(requestId)
            .methodName(method.getName())
            .paramTypes(method.getParameterTypes())
            .parameters(args)
            .service(service)
            .build();

        //服务发现
        List<ServiceInstance> serviceInstancePool = serviceDiscovery.lookup(service);

        //路由, 负载均衡
        ServiceInstance routedService = router.route(serviceInstancePool, service.getTag());

        //过滤器, 用于容错机制
        FilterChain clientBeforeSendFilter = FilterCache.get(ClientBeforeSendFilter.class);
        clientBeforeSendFilter.invokeChain(rpcRequest, routedService);

        //发送请求
        CompletableFuture<RpcResponse> future =
            (CompletableFuture<RpcResponse>) rpcTransport.sendAsync(rpcRequest, routedService);
        //阻塞
        RpcResponse rpcResponse = future.get();
        return rpcResponse.getData();
    }

    private void checkResponse(RpcRequest request, RpcResponse response) {
        if (response == null) {
            throw new RpcException("rpc response is null, request:{ %s }", request);
        }
        if (!Objects.equals(request.getRequestId(), response.getRequestId())) {
            throw new RpcException("requestId not match, request:{ %s }, response:{ %s }", request, response);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doProxy(method, args);
    }
}
