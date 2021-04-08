package rpc.zengfk.remoting.transport.netty.handler;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rpc.zengfk.model.RpcResponse;
import rpc.zengfk.protocol.RpcProtocol;
import rpc.zengfk.remoting.transport.netty.client.FutureBuffer;
import rpc.zengfk.remoting.transport.netty.client.NettyRpcClient;
import rpc.zengfk.utils.SpringContextUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * rpc响应处理器
 *
 * @author zeng.fk
 * 2021-04-06 16:38
 */
@Slf4j
@Component
public class ResponseHandler extends SimpleChannelInboundHandler<RpcProtocol> {

    /**
     * 处理 response, ByteBuf已在父类中释放
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol protocol) throws Exception {
        log.debug("ResponseHandler接受到protocol:{}", protocol);
        assert protocol != null;
        if (protocol.isHeartbeat()) {
            log.debug("received heartbeat pong");
            return;
        }
        RpcResponse rpcResponse = (RpcResponse) protocol.getData();
        FutureBuffer.complete(rpcResponse);
    }

    /**
     * 如果10s内无请求, 发送心跳包
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                InetSocketAddress server = (InetSocketAddress) ctx.channel().remoteAddress();
                log.info("10s内无请求发起, 发送心跳包至...{}", server);
                NettyRpcClient nettyRpcClient = SpringContextUtil.getBean(NettyRpcClient.class);
                Channel channel = nettyRpcClient.getServerChannel(server);
                RpcProtocol rpcMessage = RpcProtocol.builder()
                    .type(RpcProtocol.TYPE_HEARTBEAT_PING)
                    .compressor(RpcProtocol.COMPRESSION_GZIP)
                    .serializer(RpcProtocol.SERIALIZER_PROTOSTUFF)
                    .build();
                channel.writeAndFlush(rpcMessage)
                    //如果心跳包出错, 则关闭channel FIXME 心跳失败策略
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
