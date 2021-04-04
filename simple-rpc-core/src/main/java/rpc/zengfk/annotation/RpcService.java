package rpc.zengfk.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
}
