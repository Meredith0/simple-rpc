package rpc.simple.cache;

/**
 * @author zeng.fk
 * 2021-04-15 10:04
 */
public interface Cache<T> {

    void put(Long key, T value);

    T get(Long key);

    void expire();

}
