package rpc.zengfk.support.strategy;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.annotation.FailStrategy;
import rpc.zengfk.exception.BusinessException;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.support.AbstractFailStrategy;

/**
 * @author zeng.fk
 * 2021-04-13 17:33
 */
@Slf4j
@FailStrategy
public class Failfast extends AbstractFailStrategy {

    @Override
    public Object process(Object... args) {
        throw new RpcException("failfast");
    }
}
