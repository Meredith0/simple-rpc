package rpc.simple.model;

import lombok.*;

import java.io.Serializable;

/**
 * @author zeng.fk
 * 2021-04-05 16:58
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcResponse {

    public static final int OK = 200;
    public static final int MOCKED = 201;
    public static final int CLIENT_ERRORS = 400;
    public static final int SERVER_ERRORS = 500;
    public static final int BUSINESS_EXCEPTION = 600;

    private Long requestId;

    private Integer code;

    private String message;

    private Object data;

    public static RpcResponse forSuccess(Long requestId, Object data) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setData(data);
        rpcResponse.setCode(OK);
        rpcResponse.setMessage("OK");
        return rpcResponse;
    }

    public static RpcResponse forBusinessException(Long requestId, String msg) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(BUSINESS_EXCEPTION);
        rpcResponse.setMessage(msg);
        return rpcResponse;
    }



    public static RpcResponse forServerError(Long requestId, String msg) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(SERVER_ERRORS);
        rpcResponse.setMessage(msg);
        return rpcResponse;
    }
    public static RpcResponse forClientError(Long requestId) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(CLIENT_ERRORS);
        rpcResponse.setMessage("BAD CLIENT");
        return rpcResponse;
    }

    public boolean isRpcSuccess() {
        return code == OK || code == BUSINESS_EXCEPTION;
    }
}
