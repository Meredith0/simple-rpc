package rpc.zengfk.annotation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import rpc.zengfk.protocol.RpcProtocol;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 *  Rpc服务暴露注解
 * @author zeng.fk
 *     2021-04-01 22:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * 服务名称
     */
    String name();

    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 排除该类的某法, 默认暴露该类的所有public方法, 涉及的类要用全名, 类与方法之间用 # 分隔
     * eg: xxx.xxx.HelloService#sayHello(java.lang.String,java.lang.String)
     */
    String[] exclude() default {};
}
