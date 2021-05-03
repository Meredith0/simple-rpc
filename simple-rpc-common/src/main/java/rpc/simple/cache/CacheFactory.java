package rpc.simple.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.enums.CacheTypeEnum;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zeng.fk
 * 2021-04-15 10:53
 */
@Slf4j
public class CacheFactory {

    private static final Map<String, com.google.common.cache.Cache<Long, Object>> INSTANCE_MAP = Maps.newConcurrentMap();

    private CacheFactory() {}

    public static <T> com.google.common.cache.Cache<Long,T> newCache(String name, CacheTypeEnum type, int initCapacity,
                                                                  long timeoutMillis) {
        com.google.common.cache.Cache<Long, Object> cache = CacheBuilder.newBuilder()
            .initialCapacity(initCapacity)
            .expireAfterWrite(timeoutMillis, TimeUnit.MILLISECONDS).build();

        INSTANCE_MAP.put(name, cache);
        return (com.google.common.cache.Cache<Long, T>) cache;
    }

    public static <T> com.google.common.cache.Cache<Long, T> getCache(String name) {
        return (com.google.common.cache.Cache<Long, T>) INSTANCE_MAP.get(name);
    }
}
