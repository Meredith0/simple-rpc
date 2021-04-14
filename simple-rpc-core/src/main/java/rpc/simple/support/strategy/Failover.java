package rpc.simple.support.strategy;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.FailStrategy;
import rpc.simple.support.AbstractFailStrategy;

/**
 * @author zeng.fk
 * 2021-04-14 10:38
 */
@Slf4j
@FailStrategy
public class Failover extends AbstractFailStrategy {

    @Override
    public Object process(Object... args) {
        return retry(args);
    }

    private Object retry(Object[] args) {
        return null;
    }

}
