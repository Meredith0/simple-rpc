package rpc.simple.annotation;

import java.lang.annotation.*;

/**
 *  配合@RpcService 使用, 标记的方法不暴露
 * @author zeng.fk
 *     2021-04-07 22:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Exclude {
}
