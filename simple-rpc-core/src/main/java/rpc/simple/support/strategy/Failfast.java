package rpc.simple.support.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rpc.simple.annotation.FailStrategy;
import rpc.simple.annotation.RpcFilter;
import rpc.simple.exception.RpcException;
import rpc.simple.support.AbstractFailStrategy;

/**
 * @author zeng.fk
 * 2021-04-13 17:33
 */
@Slf4j
@FailStrategy
public class Failfast extends AbstractFailStrategy {

    @Override
    public Object process(Object... args) {
        throw new RpcException("failfast", args);
    }

}
