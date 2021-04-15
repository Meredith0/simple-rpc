package rpc.simple.utils;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author zeng.fk
 * 2021-04-15 16:01
 */
@Slf4j
public abstract class SingletonFactory {

    private static final Map<Class<?>, Object> INSTANCE_MAP = Maps.newConcurrentMap();

    @SneakyThrows
    protected static <T> T getInstance(Class<T> clazz) {
        Object instance = INSTANCE_MAP.get(clazz);
        if (instance != null) {
            return (T) instance;
        }
        synchronized (SingletonFactory.class) {
            if (!INSTANCE_MAP.containsKey(clazz)) {
                instance = clazz.newInstance();
                INSTANCE_MAP.put(clazz, instance);
            }
        }
        return (T) instance;
    }
}
