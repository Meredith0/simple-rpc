package rpc.simple.cache;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.cache.timeout.TimeoutCache;
import rpc.simple.enums.CacheTypeEnum;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zeng.fk
 * 2021-04-15 10:53
 */
@Slf4j
public class CacheFactory {

    private static final Map<String, Cache<?>> INSTANCE_MAP = Maps.newConcurrentMap();

    private CacheFactory() {}

    public static <T> Cache<T> newCache(String name, CacheTypeEnum type, int initCapacity, long duration) {

        Cache<?> instance = INSTANCE_MAP.get(name);
        if (instance == null) {
            synchronized (CacheFactory.class) {
                if (instance == null && type.equals(CacheTypeEnum.TIMEOUT)) {
                    instance = new TimeoutCache<>(initCapacity, duration);
                    INSTANCE_MAP.putIfAbsent(name, instance);
                }
            }
        }
        return (Cache<T>) INSTANCE_MAP.get(name);
    }

    public static Cache<?> getCache(String name) {
        return INSTANCE_MAP.get(name);
    }
}
