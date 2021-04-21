package rpc.simple.remoting.transport.netty.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rpc.simple.model.Metadata;
import rpc.simple.provider.ServiceProvider;
import rpc.simple.registry.zookeeper.ZkServiceRegistry;
import rpc.simple.remoting.transport.netty.codec.ProtocolDecoder;
import rpc.simple.remoting.transport.netty.codec.ProtocolEncoder;
import rpc.simple.remoting.transport.netty.handler.RequestHandler;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zeng.fk
 * 2021-04-05 00:20
 */
@Slf4j
@Component
public class NettyRpcServer {

    @Value("${rpc.remoting.netty.port}")
    public String PORT;
    @Autowired
    private ZkServiceRegistry zkServiceRegistry;

    private static final String THREAD_NAME_FORMAT = "netty-rpc-server";
    //netty用于临时存放已完成三次握手的请求的队列的最大长度
    private static final int HAND_SHAKEN_QUEUE_CAPACITY = 128;
    @Autowired
    private ServiceProvider serviceProvider;

    @PostConstruct
    @SneakyThrows
    public void start() {
        log.info("*************** 正在启动 netty-rpc-server... *************** ");
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
            Runtime.getRuntime().availableProcessors() + 1,
            new ThreadFactoryBuilder().setNameFormat(THREAD_NAME_FORMAT + "-%d").build()
        );
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 开启了 Nagle 算法，尽可能的发送大数据块
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 开启 TCP 底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //netty用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                .option(ChannelOption.SO_BACKLOG, HAND_SHAKEN_QUEUE_CAPACITY)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 当客户端第一次进行请求的时候才会进行初始化
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 30 秒之内没有收到客户端心跳就关闭连接
                        ChannelPipeline cp = ch.pipeline();
                        cp.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        cp.addLast(new ProtocolEncoder());
                        cp.addLast(new ProtocolDecoder());
                        cp.addLast(serviceHandlerGroup, new RequestHandler());
                    }
                });

            // 绑定端口，同步等待绑定成功
            ChannelFuture cf = bootstrap.bind(host, Integer.parseInt(PORT)).sync();
            //注册元数据
            registerMetadata();
            log.info("****************** netty-rpc-server 上线成功! ******************");

            // 等待服务端监听端口关闭
            cf.channel().closeFuture().sync();

        } finally {
            log.error("shutting down bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }

    @SneakyThrows
    private void registerMetadata() {
        Metadata metadata = new Metadata();
        metadata = zkServiceRegistry.register(metadata);
        Metadata.register(metadata);
    }
}
