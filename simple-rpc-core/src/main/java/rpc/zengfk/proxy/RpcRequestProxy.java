package rpc.zengfk.proxy;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.enums.RpcResponseEnum;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.model.RpcRequest;
import rpc.zengfk.model.RpcResponse;
import rpc.zengfk.model.Service;
import rpc.zengfk.remoting.transport.RpcTransport;
import rpc.zengfk.remoting.transport.netty.client.NettyRpcClient;

import java.lang.reflect.Method;
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

    private final RpcTransport rpcTransport;
    private final Service service;

    public RpcRequestProxy(RpcTransport rpcTransport, Service service) {
        this.rpcTransport = rpcTransport;
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        log.info("发起rpc请求, requestId:{}", requestId);

        RpcRequest rpcRequest = RpcRequest.builder()
            .requestId(requestId)
            .methodName(method.getName())
            .paramTypes(method.getParameterTypes())
            .parameters(args)
            .service(service)
            .build();
        RpcResponse rpcResponse = null;
        if (rpcTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse> future =
                (CompletableFuture<RpcResponse>) rpcTransport.sendAsync(rpcRequest);
            //阻塞
            rpcResponse = future.get();
        }
        //todo 容错机制
        checkResponse(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }

    private void checkResponse(RpcRequest request, RpcResponse response) {
        if (response == null) {
            throw new RpcException("rpc请求返回为null, request:{ %s }",request.toString());
        }
        if (!Objects.equals(request.getRequestId(), response.getRequestId())) {
            throw new RpcException("requestId不匹配, request:{ %s }, response:{ %s }", request.toString(), response.toString());
        }
        if (response.getCode() == null || !response.getCode().equals(RpcResponseEnum.SUCCESS.getCode())) {
            throw new RpcException("服务调用失败, request:{ %s }, response:{ %s }", request.toString(), response.toString());
        }
    }
}
