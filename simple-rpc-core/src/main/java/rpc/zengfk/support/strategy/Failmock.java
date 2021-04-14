package rpc.zengfk.support.strategy;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.annotation.FailStrategy;
import rpc.zengfk.support.AbstractFailStrategy;

/**
 * @author zeng.fk
 * 2021-04-14 10:40
 */
@Slf4j
@FailStrategy
public class Failmock extends AbstractFailStrategy {

    @Override
    public Object process(Object... args) {
        return null;
    }
}
