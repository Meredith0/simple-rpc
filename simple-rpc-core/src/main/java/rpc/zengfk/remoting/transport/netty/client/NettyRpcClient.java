package rpc.zengfk.remoting.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rpc.zengfk.config.ExtensionLoaderConfig;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.extension.ExtensionLoader;
import rpc.zengfk.loadBalance.LoadBalance;
import rpc.zengfk.model.RpcRequest;
import rpc.zengfk.model.RpcResponse;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.protocol.RpcProtocol;
import rpc.zengfk.registry.ServiceDiscovery;
import rpc.zengfk.remoting.transport.RpcTransport;
import rpc.zengfk.remoting.transport.netty.codec.ProtocolDecoder;
import rpc.zengfk.remoting.transport.netty.codec.ProtocolEncoder;
import rpc.zengfk.remoting.transport.netty.handler.ResponseHandler;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zeng.fk
 * 2021-04-05 16:58
 */
@Slf4j
@Component
public final class NettyRpcClient implements RpcTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final LoadBalance loadBalance;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        // log.debug("客户端正在启动...");
        this.serviceDiscovery = ExtensionLoader.ofType(ServiceDiscovery.class).getExtension(ExtensionLoaderConfig.DISCOVERY);
        this.loadBalance = ExtensionLoader.ofType(LoadBalance.class).getExtension(ExtensionLoaderConfig.LOAD_BALANCE);
        // eventLoopGroup = new NioEventLoopGroup();
        // bootstrap = new Bootstrap()
        //     .group(eventLoopGroup)
        //     .channel(NioSocketChannel.class)
        //     .handler(new LoggingHandler(LogLevel.INFO))
        //     .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000) //连接超时时间, 10s
        //     //worker的handler
        //     .handler(new ChannelInitializer<SocketChannel>() {
        //         @Override
        //         protected void initChannel(SocketChannel ch) {
        //             ChannelPipeline p = ch.pipeline();
        //             //如果10秒内没有请求, 发送一个心跳包
        //             p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
        //             p.addLast(new ProtocolEncoder());
        //             p.addLast(new ProtocolDecoder());
        //             p.addLast(new ResponseHandler());//响应处理器
        //         }
        //     });
        // log.debug("客户端启动成功! ");
    }

    @PostConstruct
    private void start() {
        log.debug("客户端正在启动...");
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap()
            .group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000) //连接超时时间, 10s
            //worker的handler
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    //如果10秒内没有请求, 发送一个心跳包
                    p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                    p.addLast(new ProtocolEncoder());
                    p.addLast(new ProtocolDecoder());
                    p.addLast(new ResponseHandler());//响应处理器
                }
            });
        log.debug("客户端启动成功! ");
    }

    @Override
    @SneakyThrows
    public Object sendAsync(RpcRequest req) {
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

        List<ServiceInstance> serviceInstancePool = serviceDiscovery.lookup(req.getService());
        //一致性哈希负载均衡的key, 默认为本机ip
        String host = InetAddress.getLocalHost().getHostAddress();
        ServiceInstance selectedService = loadBalance.select(serviceInstancePool, host);

        Channel channel = getServerChannel(selectedService.getIp());

        if (channel.isActive()) {
            FutureBuffer.put(req.getRequestId(), responseFuture);
            RpcProtocol protocol = RpcProtocol.builder()
                .type(RpcProtocol.TYPE_REQ)
                .compressor(RpcProtocol.COMPRESSION_GZIP)
                .serializer(RpcProtocol.SERIALIZER_PROTOSTUFF)
                .data(req).build();

            channel.writeAndFlush(protocol)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.debug("客户端封装协议并发送成功! protocol:{}", protocol);
                    }
                    else {
                        future.channel().close();
                        responseFuture.completeExceptionally(future.cause());
                        log.error("客户端发送协议失败:", future.cause());
                    }
                });
        }
        else {
            throw new RpcException("服务端channel关闭");
        }
        return responseFuture;
    }

    public Channel getServerChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = ChannelCache.get(inetSocketAddress);
        if (channel == null) {
            channel = connectTo(inetSocketAddress);
            ChannelCache.add(inetSocketAddress, channel);
        }
        return channel;
    }

    @SneakyThrows
    public Channel connectTo(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功! ip:{}", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            }
            else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public Object sendSync(RpcRequest request) {
        throw new UnsupportedOperationException("同步请求暂未实现");
    }

    @Override
    public Object shutdownGracefully() {
        return eventLoopGroup.shutdownGracefully(5, 10, TimeUnit.SECONDS);
    }
}




