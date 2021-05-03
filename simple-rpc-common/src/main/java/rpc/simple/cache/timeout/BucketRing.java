package rpc.simple.cache.timeout;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * 环形数组维护ConcurrentReferenceHashMap的key
 *
 * @author zeng.fk
 * 2021-04-25 23:11
 */
@Slf4j
public class BucketRing<T> {

    private static final int RING_SIZE = 12;
    private final ArrayList<Bucket<T>> RING ;
    private volatile int currentBucket = 0;
    private volatile int expiredBucket = 2;

    public BucketRing() {
        RING = new ArrayList<>(RING_SIZE);
        for (int i = 0; i < RING_SIZE; i++) {
            RING.add(new Bucket<>());
        }
    }

    public void put(T key) {
        Bucket<T> bucket = RING.get(currentBucket);
        bucket.survive(key);
    }

    public void expire() {
        Bucket<T> expired = RING.get(expiredBucket);
        log.info("清除bucket:{}", expired);
        expired.clearKeys();
        nextTick();
    }

    /**
     * 无并发, 不需要同步
     */
    private void nextTick() {
        currentBucket = (currentBucket + 1) % RING_SIZE;
        expiredBucket = (expiredBucket + 1) % RING_SIZE;
        log.info("next tick...currentBucket:{},expiredBucket:{}", currentBucket, expiredBucket);
    }

}
