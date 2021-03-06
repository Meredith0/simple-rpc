package rpc.simple.filter.lifecycle;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.filter.BreakableFilter;
import rpc.simple.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 21:02
 */
@Slf4j
public abstract class ServerReceivedFilter extends BreakableFilter<RpcProtocol, ChannelHandlerContext> {

    public Object[] filter(RpcProtocol rpcProtocol, ChannelHandlerContext serviceInstance) {
        return apply(rpcProtocol, serviceInstance, false);
    }
}
