package rpc.zengfk.support;

import rpc.zengfk.annotation.SPI;

/**
 * @author zeng.fk
 * 2021-04-13 17:18
 */
@SPI
public interface FailStrategy {

    Object process(Object... args);
}
