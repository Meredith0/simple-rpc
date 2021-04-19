package rpc.simple.utils;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author zeng.fk
 * 2021-04-15 19:01
 */
@Slf4j
public class SingletonFactory {

    private static final Map<Class<?>, Object> INSTANCE_MAP = Maps.newConcurrentMap();

    /**
     * @param clazz 单例类
     * @param args  构造器参数
     * @return 单例类实例
     */
    @SneakyThrows
    public static <T> T getInstance(Class<T> clazz, Object... args) {
        Object instance = INSTANCE_MAP.get(clazz);
        if (instance != null) {
            return (T) instance;
        }
        synchronized (SingletonFactory.class) {
            if (!INSTANCE_MAP.containsKey(clazz)) {
                Class<?>[] argsType = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsType[i] = args[i].getClass();
                }
                Constructor<T> constructor = clazz.getDeclaredConstructor(argsType);
                constructor.setAccessible(true);
                instance = constructor.newInstance(args);
                INSTANCE_MAP.put(clazz, instance);
            }
        }
        return (T) instance;
    }
}
