package rpc.simple.annotation;

import rpc.simple.model.Callback;
import rpc.simple.support.enums.FailStrategyEnum;

import java.lang.annotation.*;
import java.util.function.BiConsumer;

/**
 *  标注某个方法为远程方法, 该方法的调用会被RpcClientProxy代理
 * @author zeng.fk
 *     2021-04-01 22:07
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {


    /**
     * 服务名称
     */
    String name();

    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 路由键
     */
    String tag() default "";

    /**
     * 指定了回调接口即表示使用异步调用, 原方法会直接返回null
     */
    Class<? extends Callback> callback() default Callback.class;

    /**
     * 容错策略, 默认FAILFAST
     */
    FailStrategyEnum failStrategy() default FailStrategyEnum.FAIL_FAST;
}
