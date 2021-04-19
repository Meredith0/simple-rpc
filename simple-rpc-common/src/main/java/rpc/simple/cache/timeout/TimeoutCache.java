package rpc.simple.cache.timeout;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ConcurrentReferenceHashMap;
import rpc.simple.cache.Cache;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 定时过期的cache, 如果key在超时时间内没有被访问, 就会被移除
 * @author zeng.fk
 * 2021-04-14 18:16
 */
@Slf4j
public class TimeoutCache<T> implements Cache<T> {

    private final Map<Long, T> CACHE_MAP;
    private final Bucket bucket;
    @Getter
    private final long timeout;

    public TimeoutCache(int initCapacity, long duration) {
        this.timeout = duration;
        this.bucket = new Bucket();
        this.CACHE_MAP = new ConcurrentReferenceHashMap<>(initCapacity, ConcurrentReferenceHashMap.ReferenceType.WEAK);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::scheduledExpire, duration, duration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void put(Long key, T value) {
        bucket.survive(key);
        CACHE_MAP.put(key, value);
    }

    @Override
    public T remove(Long key) {
        return CACHE_MAP.remove(key);
    }

    @Override
    public T get(Long key) {
        bucket.survive(key);
        return CACHE_MAP.get(key);
    }

    @Override
    public void expire() {
        bucket.clearExpiredKey();
    }

    private void scheduledExpire() {
        expire();
    }
}
