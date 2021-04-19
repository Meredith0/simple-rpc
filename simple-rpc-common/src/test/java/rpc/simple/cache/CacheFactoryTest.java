package rpc.simple.cache;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import rpc.simple.enums.CacheTypeEnum;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CacheFactoryTest {

    @Test
    void newCache() {
        Cache<Object> testcache = CacheFactory.newCache("testcache", CacheTypeEnum.TIMEOUT, 10, 2000);
        testcache.put(123451L, "1");
        testcache.put(123452L, "2");
        testcache.put(123453L, "3");
        testcache.put(123454L, "4");
    }

    @SneakyThrows
    @Test
    void getCache() {
        Cache<?> testcache = CacheFactory.getCache("testcache");
        String s = (String) testcache.get(123451L);
        assertEquals(s,"1");
        Thread.sleep(4000);
        System.gc();

        Object o = testcache.get(123451L);
        assertNull(o);
    }

}