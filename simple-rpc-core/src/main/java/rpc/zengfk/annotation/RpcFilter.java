package rpc.zengfk.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标记一个过滤器
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
}
