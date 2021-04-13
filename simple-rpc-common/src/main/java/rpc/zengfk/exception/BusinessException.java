package rpc.zengfk.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 服务端业务异常, 会被ExceptionFilter捕获
 * @author zeng.fk
 * 2021-04-12 23:26
 */
@Slf4j
public class BusinessException extends RuntimeException {


    public BusinessException(String message) {
        super(message);
    }


    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
