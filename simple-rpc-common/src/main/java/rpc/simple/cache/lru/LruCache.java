package rpc.simple.cache.lru;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.cache.Cache;

/**
 * @author zeng.fk
 * 2021-04-15 10:11
 */
@Slf4j
public class LruCache<T> implements Cache<T> {

    @Override
    public void put(Long key, Object value) {

    }

    @Override
    public T get(Long key) {
        return null;
    }

    @Override
    public void expire() {

    }
}
