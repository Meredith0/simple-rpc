package rpc.zengfk.filter.lifecycle;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.filter.BreakableFilter;
import rpc.zengfk.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 21:13
 */
@Slf4j
public abstract class ClientReceivedFilter extends BreakableFilter<RpcProtocol, ChannelHandlerContext> {

    public Object[] filter(RpcProtocol rpcProtocol, ChannelHandlerContext ctx) {
        return apply(rpcProtocol, ctx);
    }
}
