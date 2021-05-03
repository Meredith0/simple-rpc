package rpc.simple.cache.timeout;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ConcurrentReferenceHashMap;
import rpc.simple.cache.Cache;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * FIXME 有bug, key未被BucketRing移除但依然被gc, 先用guava cache代替
 * 定时过期的cache, 如果key在超时时间内没有被访问, 就会被移除
 * 由2部分构成:
 * ConcurrentReferenceHashMap用于缓存 k-v, 弱引用, 如果key没有被其他对象持有就会在下次gc回收
 * BucketRing用于维护存活的key
 *
 * @author zeng.fk
 * 2021-04-14 18:16
 */
@Slf4j
public class TimeoutCache<T> implements Cache<Long,T> {

    //缓存
    private final Map<Long, T> CACHE_MAP;
    private final BucketRing<Long> bucketRing;
    @Getter
    private final long timeout;

    public TimeoutCache(int initCapacity, long duration) {
        this.timeout = duration;
        this.bucketRing = new BucketRing<>();
        this.CACHE_MAP = new ConcurrentReferenceHashMap<>(initCapacity, ConcurrentReferenceHashMap.ReferenceType.WEAK);
        //只有一个任务, 不存在BlockingQueue溢出的情况
        Executors.newScheduledThreadPool(1)
            .scheduleAtFixedRate(this::scheduledExpire, duration, duration, TimeUnit.MILLISECONDS);
    }


    @Override
    public void put(Long key, T value) {
        bucketRing.put(key);
        CACHE_MAP.put(key, value);
        log.info("put key:{}, 当前 CACHE_MAP:{}",key,CACHE_MAP.entrySet());
    }

    @Override
    public T remove(Long key) {
        return CACHE_MAP.remove(key);
    }

    @Override
    public T get(Long key) {
        log.info("get key:{}, 当前CACHE_MAP:{}", key,CACHE_MAP.entrySet());
        bucketRing.put(key);
        return CACHE_MAP.get(key);
    }

    @Override
    public void expire() {
        bucketRing.expire();
    }

    private void scheduledExpire() {
        log.info("调用 expire...");
        expire();
    }
}
