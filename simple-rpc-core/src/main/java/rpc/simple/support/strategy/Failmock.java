package rpc.simple.support.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import rpc.simple.annotation.FailStrategy;
import rpc.simple.support.AbstractFailStrategy;

/**
 * @author zeng.fk
 * 2021-04-14 10:40
 */
@Slf4j
@FailStrategy
public class Failmock extends AbstractFailStrategy {

    public Failmock() {
    }

    @Override
    public Object process(Object... args) {
        return null;
    }
}
