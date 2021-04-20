package rpc.simple.annotation;

import rpc.simple.support.enums.FailStrategyEnum;
import rpc.simple.support.strategy.FailMock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
     * 异步调用时的回调接口
     */
    BiConsumer<?, ?> callback = null;

    /**
     * 容错策略, 默认FAILFAST
     */
    FailStrategyEnum failStrategy() default FailStrategyEnum.FAIL_FAST;

    /**
     * 服务降级接口, 仅在failStrategy==FailStrategyEnum.FAIL_MOCK 时启用
     * 传入降级的实现类(实现rpc服务接口), 入参同该服务
     */
    // Class<? extends FailMock> mock() default FailMock.class;
}
