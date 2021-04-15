package rpc.simple.cache.timeout;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 记录存活的key, 类似年轻代GC, s0和s1轮流作为存活区,
 * key被移除后, 由于Cache使用弱引用map, 就会被gc
 * @author zeng.fk
 * 2021-04-15 10:02
 */
@Slf4j
public class Bucket {

    private final Set<Long> s0;
    private final Set<Long> s1;
    private Set<Long> survivor;

    public Bucket() {
        s0 = Sets.newConcurrentHashSet();
        s1 = Sets.newConcurrentHashSet();
        survivor = s0;
    }

    public void clearExpiredKey() {
        synchronized (this) {
            log.debug("clearing expired set...");
            Set<Long> expiredSet = getExpiredSet();
            flip();
            expiredSet.clear();
        }
    }

    public void survive(Long key) {
        if (!survivor.contains(key)) {
            survivor.add(key);
        }
    }

    private Set<Long> getExpiredSet() {
        return survivor;
    }

    private void flip() {
        survivor = (survivor == s0 ? s1 : s0);
    }
}
