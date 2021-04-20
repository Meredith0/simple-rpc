package rpc.simple.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author zeng.fk
 * 2021-04-20 19:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface MockService {
    /**
     * 服务名称, 应与@RpcService中一致
     */
    String name();

}
