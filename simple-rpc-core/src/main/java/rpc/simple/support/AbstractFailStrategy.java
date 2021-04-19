package rpc.simple.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zeng.fk
 * 2021-04-13 17:24
 */
@Slf4j
public abstract class AbstractFailStrategy implements FailStrategy {

    public Object handle(Object... args) {

        return process(args);
    }
}
