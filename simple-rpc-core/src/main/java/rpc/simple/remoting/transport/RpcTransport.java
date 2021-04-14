package rpc.simple.remoting.transport;

import rpc.simple.model.RpcRequest;
import rpc.simple.model.ServiceInstance;

/**
 * @author zeng.fk
 * 2021-04-05 16:50
 */
public interface RpcTransport {

    /**
     * 异步请求
     */
    Object sendAsync(RpcRequest request, ServiceInstance serviceInstance);

    /**
     * 同步请求
     */
    Object sendSync(RpcRequest request);

    /**
     * 优雅停机
     */
    Object shutdownGracefully();
}
