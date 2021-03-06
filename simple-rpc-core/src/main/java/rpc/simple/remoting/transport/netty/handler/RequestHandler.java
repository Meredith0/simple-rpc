package rpc.simple.remoting.transport.netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.exception.BusinessException;
import rpc.simple.exception.RpcException;
import rpc.simple.cache.system.FilterCache;
import rpc.simple.filter.FilterChain;
import rpc.simple.filter.lifecycle.ServerReceivedFilter;
import rpc.simple.filter.lifecycle.ServerSentFilter;
import rpc.simple.invoker.ServiceInvoker;
import rpc.simple.model.RpcRequest;
import rpc.simple.model.RpcResponse;
import rpc.simple.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-06 22:13
 */
@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RpcProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol protocol) {
        //是心跳就直接返回
        if (protocol.isHeartbeat()) {
            protocol.answerHeartbeat();
            log.debug("RequestHandler响应心跳: {}...", protocol);
            return;
        }
        //过滤器
        FilterChain serverReceivedFilterChain = FilterCache.get(ServerReceivedFilter.class);
        serverReceivedFilterChain.invokeChain(protocol, ctx);

        log.debug("RequestHandler received protocol: {} ", protocol);

        RpcRequest rpcRequest = (RpcRequest) protocol.getBody();
        RpcResponse rpcResponse = null;
        try {
            //调用服务
            Object res = ServiceInvoker.getInstance().accept(rpcRequest);
            rpcResponse = RpcResponse.forSuccess(rpcRequest.getRequestId(), res);
        } catch (BusinessException | RpcException e) {
            e.printStackTrace();
            if (e instanceof BusinessException) {
                rpcResponse = RpcResponse.forBusinessException(rpcRequest.getRequestId(), e.getMessage());
            }
            if (e instanceof RpcException) {
                rpcResponse = RpcResponse.forServerError(rpcRequest.getRequestId(), e.getMessage());
            }
        }
        protocol.setType(RpcProtocol.TYPE_RESP);
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            protocol.setBody(rpcResponse);
        } else {
            rpcResponse = RpcResponse.forClientError(rpcRequest.getRequestId());
            protocol.setBody(rpcResponse);
            log.error("channel is NOT writable, protocol dropped {}", protocol);
        }

        ctx.writeAndFlush(protocol).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        //过滤器
        FilterChain serverSentFilterChain = FilterCache.get(ServerSentFilter.class);
        serverSentFilterChain.invokeChain(protocol, rpcResponse);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("连续30s未收到客户端心跳，关闭连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("RequestHandler catches exception: ");
        cause.printStackTrace();
        ctx.close();
    }

}
