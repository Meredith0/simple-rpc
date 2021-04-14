package rpc.simple.annotation;

import java.lang.annotation.*;

/**
 *  Rpc方法暴露注解
 * @author zeng.fk
 *     2021-04-07 22:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface RpcMethod {

    String name();

    String version() default "1.0.0";
}
