package rpc.simple.filter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zeng.fk
 * 2021-04-12 18:06
 */
@Slf4j
public abstract class BreakableFilter<T, S> implements Filter<T, S> {

    /**
     * 中止过滤器
     */
    private volatile Boolean BREAK_FLAG = false;

    public Object[] apply(T t, S s, Boolean isBreak) {
        if (isBreak) {
            return new Object[]{t, s, true};
        }
        Object[] filtered = doFilter(t, s);
        return new Object[]{filtered[0], filtered[1], BREAK_FLAG};
    }

    protected void breakFilter() {
        this.BREAK_FLAG = true;
    }

    protected abstract Object[] filter(T t, S s);
}
