package rpc.simple.extension;

/**
 * @author zeng.fk
 *     2021-04-04 22:29
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
