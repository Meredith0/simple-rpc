package rpc.zengfk.proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author zeng.fk
 *     2021-04-02 16:16
 */
public interface Invoker extends InvocationHandler {

    @Override
    default Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

}
