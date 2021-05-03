package rpc.simple.cache;

import lombok.SneakyThrows;
import org.jboss.netty.util.internal.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rpc.simple.enums.CacheTypeEnum;

import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CacheFactoryTest {

    @BeforeAll
    static void setup() {
    }

    @Test
    void newCache() {
        com.google.common.cache.Cache<Long, Object> testcache = CacheFactory.newCache("testcache",
            CacheTypeEnum.TIMEOUT, 10, 100);
        testcache.put(123451L, "1");
        testcache.put(123452L, "2");
        System.gc();
        testcache.put(123453L, "3");
        testcache.put(123454L, "4");
    }

    @SneakyThrows
    @Test
    void getCache() {
        com.google.common.cache.Cache<Long, Object> testcache = CacheFactory.getCache("testcache");

        ThreadLocalRandom threadLocalRandom = new ThreadLocalRandom();
        Long l = threadLocalRandom.nextLong();
        testcache.put(l, "123");
        System.gc();
        Object o = testcache.getIfPresent(l);
        assertNotNull(o);

        Long l1 = threadLocalRandom.nextLong();
        testcache.put(l1, "223");
        LockSupport.parkNanos(200 * 1000 * 1000);
        Object o1 = testcache.getIfPresent(l);
        assertNull(o1);

        // for (int i = 0; i < 50; i++) {
        //     ThreadLocalRandom threadLocalRandom = new ThreadLocalRandom();
        //     Long l = threadLocalRandom.nextLong();
        //     testcache.put(l,"123");
        //     System.gc();
        //     Thread.sleep(1000);
        //     Object o = testcache.getIfPresent(l);
        //     assertNotNull(o);
        // }
    }

}