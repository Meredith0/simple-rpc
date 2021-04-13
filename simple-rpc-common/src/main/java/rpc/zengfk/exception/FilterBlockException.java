package rpc.zengfk.exception;

/**
 * 用于中断过滤器
 *
 * @author zeng.fk
 * 2021-04-12 17:07
 */
public class FilterBlockException extends RuntimeException {

    public FilterBlockException(String message) {
        super(message);
    }
}
