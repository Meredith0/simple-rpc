package rpc.zengfk.annotation;

import org.springframework.context.annotation.Bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    // String method();

    // Class<?>[] paramTypes();

    /**
     * 版本号
     */
    String version() default "1.0.0";

    byte failStrategy() default (byte)0x01;
}
