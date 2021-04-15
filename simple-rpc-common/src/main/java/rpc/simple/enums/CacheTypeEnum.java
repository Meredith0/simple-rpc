package rpc.simple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.cache.Cache;
import rpc.simple.cache.lru.LruCache;
import rpc.simple.cache.timeout.TimeoutCache;
import sun.misc.LRUCache;

/**
 * @author zeng.fk
 * 2021-04-15 16:12
 */
@Slf4j
@AllArgsConstructor
@Getter
public enum CacheTypeEnum {
    LRU((byte) 0x01, LruCache.class),
    TIMEOUT((byte) 0x02, TimeoutCache.class),
    ;

    private final byte code;
    private final Class<? extends Cache> clazz;

    public static Class<? extends Cache> get(byte code) {
        for (CacheTypeEnum c : CacheTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.clazz;
            }
        }
        return null;
    }
}
