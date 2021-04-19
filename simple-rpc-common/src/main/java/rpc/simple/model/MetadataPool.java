package rpc.simple.model;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * @author zeng.fk
 * 2021-04-19 20:08
 */
@Slf4j
@Data
public class MetadataPool {

    private static final Set<Integer> datacenterPool = Sets.newConcurrentHashSet();
    private static final Set<Integer> workerPool = Sets.newConcurrentHashSet();

    public static Integer getDatacenterId(List<String> registered) {
        return getFromPool(registered, datacenterPool);
    }

    public static Integer getWorkerId(List<String> registered) {
        return getFromPool(registered, workerPool);
    }

    private static Integer getFromPool(List<String> registered, Set<Integer> pool) {
        pool.clear();
        for (int i = 1; i <= 31; i++) {
            pool.add(i);
        }
        registered.forEach(r -> pool.remove(Integer.parseInt(r)));
        if (pool.isEmpty()) {
            throw new IllegalStateException();
        }
        return pool.iterator().next();
    }
}
