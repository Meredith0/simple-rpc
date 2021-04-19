package rpc.simple.annotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author zeng.fk
 * 2021-04-13 18:21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface FailStrategy {

    byte code() default (byte) 0x01;
}
