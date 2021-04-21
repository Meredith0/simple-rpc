package rpc.simple.model;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.exception.BusinessException;
import rpc.simple.exception.RpcException;

import java.util.function.BiConsumer;

/**
 * 回调接口
 *
 * @author zeng.fk
 * 2021-04-21 20:13
 */
@Slf4j
public abstract class Callback implements BiConsumer<Object, Throwable> {

    /**
     * @param o rpc方法返回结果
     * @param throwable rpc方法异常
     */
    @SneakyThrows
    @Override
    public void accept(Object o, Throwable throwable) {
        if (throwable != null) {
            throw throwable;
        }
        process(o);
    }

    /**
     * 回调接口
     */
    public abstract void process(Object res);
}
