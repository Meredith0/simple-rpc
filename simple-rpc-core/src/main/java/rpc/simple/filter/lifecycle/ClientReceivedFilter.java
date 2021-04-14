package rpc.simple.filter.lifecycle;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.filter.BreakableFilter;
import rpc.simple.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 23:13
 */
@Slf4j
public abstract class ClientReceivedFilter extends BreakableFilter<RpcProtocol, ChannelHandlerContext> {

    public Object[] filter(RpcProtocol rpcProtocol, ChannelHandlerContext ctx) {
        return apply(rpcProtocol, ctx, false);
    }
}
