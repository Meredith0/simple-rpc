package rpc.simple.support.strategy;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.FailStrategy;
import rpc.simple.exception.RpcException;
import rpc.simple.support.FailTolerate;

/**
 * @author zeng.fk
 * 2021-04-13 17:33
 */
@Slf4j
@FailStrategy
public class Failfast implements FailTolerate {

    @Override
    public void process(Object... args) {
        throw new RpcException("failfast", args);
    }

}
