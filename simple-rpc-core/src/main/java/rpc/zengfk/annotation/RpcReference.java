package rpc.zengfk.annotation;
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
@Target({ElementType.TYPE})
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
}
