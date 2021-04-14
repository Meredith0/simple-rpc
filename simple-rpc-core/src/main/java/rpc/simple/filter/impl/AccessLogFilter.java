package rpc.simple.filter.impl;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.RpcFilter;
import rpc.simple.filter.lifecycle.ServerReceivedFilter;
import rpc.simple.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 19:51
 */
@Slf4j
@RpcFilter
public class AccessLogFilter extends ServerReceivedFilter {

    @Override
    public Object[] doFilter(RpcProtocol rpcProtocol, ChannelHandlerContext channelHandlerContext) {
        log.info("Server Accessed! {}", rpcProtocol);
        return new Object[]{rpcProtocol, channelHandlerContext};
    }
}
