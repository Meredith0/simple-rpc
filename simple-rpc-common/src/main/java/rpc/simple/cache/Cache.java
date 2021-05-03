package rpc.simple.cache;

/**
 * @author zeng.fk
 * 2021-04-15 10:04
 */
@Deprecated
public interface Cache<K,V> {

    void put(K key, V value);

    V remove(K key);

    V get(K key);

    void expire();

}
