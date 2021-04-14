package rpc.zengfk.support;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author zeng.fk
 * 2021-04-13 18:18
 */
@Slf4j
public class FailStrategyCache {

    private static final Map<Class<? extends FailStrategy>, FailStrategy> CACHE_MAP = Maps.newConcurrentMap();

    public static void add(Class<?> classname, FailStrategy strategy) {

        if (strategy == null) {
            throw new IllegalStateException();
        }
        CACHE_MAP.put((Class<? extends FailStrategy>) classname, strategy);
    }

    public static <T> FailStrategy get(Class<T> classname) {
        return CACHE_MAP.get(classname);
    }
}
