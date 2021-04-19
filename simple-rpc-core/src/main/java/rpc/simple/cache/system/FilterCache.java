package rpc.simple.cache.system;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.filter.Filter;
import rpc.simple.filter.FilterChain;
import rpc.simple.filter.FilterNode;

import java.util.Map;

/**
 * Filter缓存
 * @author zeng.fk
 * 2021-04-12 21:44
 */
@Slf4j
public class FilterCache {

    /**
     * key: lifecycle filter classname, value: FilterChain
     */
    private static final Map<Class<? extends Filter<?,?>>, FilterChain> CACHE_MAP = Maps.newConcurrentMap();

    @SneakyThrows
    public static void add(Class<?> name, Object filter,int priority) {
        if (!(filter instanceof Filter)) {
            throw new IllegalStateException(filter + " is NOT instanceof rpc.simple.filter.Filter");
        }
        FilterChain chain = CACHE_MAP.get(name);
        if (chain == null) {
            chain = new FilterChain();
        }
        chain.add(new FilterNode((Filter<?,?>) filter, priority));
        CACHE_MAP.put((Class<? extends Filter<?, ?>>) name, chain);
    }

    public static FilterChain get(Class<? extends Filter<?,?>> name) {

        return CACHE_MAP.getOrDefault(name, new FilterChain());
    }
}
