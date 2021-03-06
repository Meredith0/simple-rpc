package rpc.simple.support;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zeng.fk
 * 2021-04-13 17:24
 */
@Slf4j
public abstract class AbstractFailTolerate implements FailTolerate {

    public void handle(Object... args) {

        process(args);
    }
}
