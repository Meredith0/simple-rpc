package rpc.zengfk.proxy;
import rpc.zengfk.annotation.SPI;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 客户端代理, 标记了@RpcReference的调用会被此类代理
 * 不同实现区别为容错机制
 * @author zeng.fk
 *     2021-04-02 19:16
 */
@SPI
public interface Proxy extends InvocationHandler {

    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

}
