package rpc.zengfk.filter.lifecycle;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.filter.BreakableFilter;

import java.lang.reflect.Method;

/**
 * @author zeng.fk
 * 2021-04-12 20:01
 */
@Slf4j
public abstract class ClientInvokedFilter extends BreakableFilter<Method, Object[]> {

    public Object[] filter(Method method, Object[] args) {
        return apply(method, args, false);
    }
}
