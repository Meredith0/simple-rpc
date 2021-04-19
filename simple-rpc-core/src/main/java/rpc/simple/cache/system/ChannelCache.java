package rpc.simple.cache.system;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author zeng.fk
 * 2021-04-05 11:10
 */
@Slf4j
public final class ChannelCache {

    //key: ip  value: channel
    private static final Map<String, Channel> CACHE = Maps.newConcurrentMap();

    public static Channel get(InetSocketAddress ip) {
        Channel channel = CACHE.get(ip.toString());
        if (channel == null) {
            return null;
        }
        if (!channel.isActive()) {
            CACHE.remove(ip.toString());
        }
        return channel;
    }

    public static void add(InetSocketAddress ip, Channel channel) {
        if (channel.isActive()) {
            CACHE.put(ip.toString(), channel);
            return;
        }
        log.warn("The channel is NOT active, refuse to add to cache, ip:{}, channel:{}", ip, channel);
    }
}
