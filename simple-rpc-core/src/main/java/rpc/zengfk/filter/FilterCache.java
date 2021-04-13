package rpc.zengfk.filter;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Filter缓存, 非线程安全
 * @author zeng.fk
 * 2021-04-12 21:44
 */
@Slf4j
public class FilterCache {

    /**
     * key: lifecycle filter classname, value: FilterChain
     */
    private static final Map<Class<?>, FilterChain<?>> CACHE_MAP = Maps.newConcurrentMap();

    @SneakyThrows
    public static void add(Class<?> name, Object filter) {
        if (!(filter instanceof Filter)) {
            throw new IllegalStateException(filter + " is NOT instanceof rpc.zengfk.filter.Filter");
        }
        FilterChain<?> chain = CACHE_MAP.get(name);
        if (chain == null) {
            chain = new FilterChain();
        }
        chain.add(new FilterNode((Filter) filter));
        CACHE_MAP.put(name, chain);
    }

    public static <T> FilterChain<T> get(Class<T> name) {

        return (FilterChain<T>) CACHE_MAP.getOrDefault(name, new FilterChain<>());
    }
}
