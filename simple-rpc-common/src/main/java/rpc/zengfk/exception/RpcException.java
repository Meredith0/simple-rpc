package rpc.zengfk.exception;

import java.util.logging.Formatter;

/**
 * @author zeng.fk
 *     2021-04-01 19:30
 */
public class RpcException extends RuntimeException {


    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }
    public RpcException(String format, Object... args) {
        super(String.format(format, args));
    }

}
