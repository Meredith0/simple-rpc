package rpc.simple.cache.timeout;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author zeng.fk
 * 2021-04-15 10:02
 */
@Slf4j
class Bucket<T> {

    private final Set<T> SURVIVED_KEYS;

    public Bucket() {
        SURVIVED_KEYS = Sets.newConcurrentHashSet();
    }

    public void clearKeys() {
        log.info("bucket:{} clearing {} keys", this, SURVIVED_KEYS);
        SURVIVED_KEYS.clear();
    }

    public void survive(T key) {
        if (!SURVIVED_KEYS.contains(key)) {
            log.info("putting key:{} into bucket:{}", key, this);
            SURVIVED_KEYS.add(key);
        }
    }
}
