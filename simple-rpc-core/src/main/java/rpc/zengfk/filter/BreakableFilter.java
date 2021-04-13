package rpc.zengfk.filter;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.exception.FilterBlockException;

/**
 * @author zeng.fk
 * 2021-04-22 18:06
 */
@Slf4j
public abstract class BreakableFilter<T, S> implements Filter<T,S> {

    /**
     * 中止过滤器
     */
    protected volatile boolean BLOCK;

    public Object[] apply(T t, S s) {
        Object[] filtered = doFilter(t, s);
        if (BLOCK) {
            log.warn("Filter blocks the request...");
            throw new FilterBlockException("Filter blocks the request...");
        }
        return filtered;
    }

    protected abstract Object[] filter(T t, S s);

}
