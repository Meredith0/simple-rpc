package rpc.zengfk.filter;

import rpc.zengfk.annotation.SPI;

/**
 * @author zeng.fk
 * 2021-04-12 19:15
 */
@SPI
public interface Filter<T, S> {

    Object[] doFilter(T t, S s);
}
