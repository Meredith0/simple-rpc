package rpc.simple.filter;

import rpc.simple.annotation.SPI;

/**
 * @author zeng.fk
 * 2021-04-12 19:15
 */
public interface Filter<T, S> {

    Object[] doFilter(T t, S s);
}
