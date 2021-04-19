package rpc.simple.cache.system;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.model.Service;
import rpc.simple.support.FailStrategy;
import rpc.simple.support.enums.FailStrategyEnum;

import java.util.Map;

/**
 * @author zeng.fk
 * 2021-04-13 18:18
 */
@Slf4j
public class FailStrategyCache {

    private static final Map<Class<? extends FailStrategy>, FailStrategy> STRATEGY = Maps.newConcurrentMap();
    private static final Map<Service, FailStrategyEnum> CONFIG = Maps.newConcurrentMap();

    public static void put(Class<?> classname, FailStrategy strategy) {

        if (strategy == null) {
            throw new IllegalStateException();
        }
        STRATEGY.put((Class<? extends FailStrategy>) classname, strategy);
    }

    public static void put(Service service, FailStrategyEnum strategyEnum) {

        if (strategyEnum == null) {
            throw new IllegalStateException();
        }
        CONFIG.put(service, strategyEnum);
    }

    public static <T> FailStrategy getStrategy(Class<T> classname) {
        return STRATEGY.get(classname);
    }

    public static FailStrategyEnum getConfig(Service service) {
        return CONFIG.get(service);
    }
}
