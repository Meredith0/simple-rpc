package rpc.zengfk.exception;

/**
 * @author zeng.fk
 *     2021-04-01 16:30
 */
public class RpcException extends RuntimeException {


    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

}
