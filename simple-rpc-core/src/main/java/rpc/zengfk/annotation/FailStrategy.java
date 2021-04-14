package rpc.zengfk.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
