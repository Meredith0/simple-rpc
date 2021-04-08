package rpc.zengfk.remoting.transport;

import rpc.zengfk.annotation.SPI;
import rpc.zengfk.model.RpcRequest;

/**
 * @author zeng.fk
 * 2021-04-05 16:50
 */
public interface RpcTransport {

    /**
     * 异步请求
     */
    Object sendAsync(RpcRequest request);

    /**
     * 同步请求
     */
    Object sendSync(RpcRequest request);

    /**
     * 优雅停机
     */
    Object shutdownGracefully();
}
