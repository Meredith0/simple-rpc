package rpc.simple.remoting.transport.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rpc.simple.filter.FilterCache;
import rpc.simple.filter.FilterChain;
import rpc.simple.filter.lifecycle.ClientReceivedFilter;
import rpc.simple.model.RpcResponse;
import rpc.simple.protocol.RpcProtocol;
import rpc.simple.remoting.transport.netty.client.FutureBuffer;
import rpc.simple.remoting.transport.netty.client.NettyRpcClient;
import rpc.simple.utils.SpringContextUtil;

import java.net.InetSocketAddress;

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
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol protocol) {
        assert protocol != null;
        if (protocol.isHeartbeat()) {
            log.debug("received heartbeat pong");
            return;
        }
        log.debug("ResponseHandler接受到protocol:{}", protocol);

        //过滤器
        FilterChain chain = FilterCache.get(ClientReceivedFilter.class);
        chain.invokeChain(protocol, ctx);

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
                Channel channel;
                NettyRpcClient nettyRpcClient = (NettyRpcClient) SpringContextUtil.getBean("nettyRpcClient");
                channel = nettyRpcClient.getServerChannel(server);
                RpcProtocol protocol = RpcProtocol.builder()
                    .type(RpcProtocol.TYPE_HEARTBEAT_PING)
                    .compressor(RpcProtocol.COMPRESSION_GZIP)
                    .serializer(RpcProtocol.SERIALIZER_PROTOSTUFF)
                    .build();
                log.debug("10s内无请求发起, 发送心跳包:{}至...{}", protocol, server);
                channel.writeAndFlush(protocol)
                    //如果心跳包出错, 则关闭channel FIXME 心跳失败策略
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        else {
            super.userEventTriggered(ctx, evt);
        }
    }

    // @Override
    // public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    //     log.error("RequestHandler catches exception:{}", cause.getMessage());
    //     ctx.close();
    // }
}
