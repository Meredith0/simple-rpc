package rpc.zengfk.proxy;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.enums.RpcResponseEnum;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.extension.ExtensionLoader;
import rpc.zengfk.extension.ExtensionName;
import rpc.zengfk.filter.Filter;
import rpc.zengfk.filter.FilterCache;
import rpc.zengfk.filter.FilterChain;
import rpc.zengfk.filter.lifecycle.ClientInvokedFilter;
import rpc.zengfk.model.RpcRequest;
import rpc.zengfk.model.RpcResponse;
import rpc.zengfk.model.Service;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.registry.ServiceDiscovery;
import rpc.zengfk.remoting.transport.RpcTransport;
import rpc.zengfk.router.Router;

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
        FilterChain<ClientInvokedFilter> chain = FilterCache.get(ClientInvokedFilter.class);
        chain.invokeChain(method, args);

        RpcRequest rpcRequest = RpcRequest.builder()
            .requestId(requestId)
            .methodName(method.getName())
            .paramTypes(method.getParameterTypes())
            .parameters(args)
            .service(service)
            .build();

        //服务发现
        List<ServiceInstance> serviceInstancePool = serviceDiscovery.lookup(rpcRequest.getService());

        //路由, 负载均衡
        ServiceInstance routedService = router.route(serviceInstancePool, service.getTag());

        //发送请求
        CompletableFuture<RpcResponse> future =
            (CompletableFuture<RpcResponse>) rpcTransport.sendAsync(rpcRequest, routedService);
        //阻塞
        RpcResponse rpcResponse = future.get();
        //todo 容错机制
        checkResponse(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }

    private void checkResponse(RpcRequest request, RpcResponse response) {
        if (response == null) {
            throw new RpcException("rpc请求返回为null, request:{ %s }", request);
        }
        if (!Objects.equals(request.getRequestId(), response.getRequestId())) {
            throw new RpcException("requestId不匹配, request:{ %s }, response:{ %s }", request, response);
        }
        if (response.getCode() == null || !response.getCode().equals(RpcResponseEnum.SUCCESS.getCode())) {
            throw new RpcException("服务调用失败, request:{ %s }, response:{ %s }", request, response);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doProxy(method, args);
    }
}
