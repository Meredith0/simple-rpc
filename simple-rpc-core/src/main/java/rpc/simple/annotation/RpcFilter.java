package rpc.simple.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * rpc请求生命周期过滤器
 *
 * @author zeng.fk
 * 2021-04-12 19:51
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface RpcFilter {

    /**
     * 优先级, 0-10
     */
    int priority() default 4;
}
