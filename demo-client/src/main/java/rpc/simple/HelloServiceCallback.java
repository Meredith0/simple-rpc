package rpc.simple;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.model.Callback;

/**
 * @author zeng.fk
 * 2021-04-21 20:11
 */
@Slf4j
public class HelloServiceCallback extends Callback {

    @Override
    public void process(Object res) {
        log.info("received callback... res: {}", res);
    }
}
