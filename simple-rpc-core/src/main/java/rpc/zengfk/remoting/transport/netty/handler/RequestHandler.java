package rpc.zengfk.remoting.transport.netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.invoker.ServiceInvoker;
import rpc.zengfk.model.RpcRequest;
import rpc.zengfk.model.RpcResponse;
import rpc.zengfk.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-06 22:13
 */
@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RpcProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol protocol) {
        log.info("RequestHandler接收到protocol: {} ", protocol);

        //是心跳就直接返回
        if (protocol.isHeartbeat()) {
            protocol.answerHeartbeat();
            return;
        }
        RpcRequest rpcRequest = (RpcRequest) protocol.getData();
        Object res = ServiceInvoker.getInstance().accept(rpcRequest);
        protocol.setType(RpcProtocol.TYPE_RESP);

        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            RpcResponse rpcResponse = RpcResponse.forSuccess(rpcRequest.getRequestId(), res);
            protocol.setData(rpcResponse);
        }
        else {
            RpcResponse rpcResponse = RpcResponse.forFail(rpcRequest.getRequestId());
            protocol.setData(rpcResponse);
            log.error("channel is NOT writable, protocol dropped {}", protocol);
        }

        ctx.writeAndFlush(protocol).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("已经30s未收到客户端心跳，关闭连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("RequestHandler catches exception");
        cause.printStackTrace();
        ctx.close();
    }

}
