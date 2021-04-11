package rpc.zengfk.model;

import lombok.*;
import rpc.zengfk.loadBalance.LoadBalance;

import java.io.Serializable;

/**
 * @author zeng.fk
 * 2021-04-05 16:58
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 45222463124123243L;
    private static final int OK = 200;
    private static final int CLIENT_ERRORS = 400;
    private static final int SERVER_ERRORS = 500;

    private String requestId;

    private Integer code;

    private String message;

    private Object data;

    public static RpcResponse forSuccess(String requestId, Object data) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setData(data);
        rpcResponse.setCode(OK);
        rpcResponse.setMessage("OK");
        return rpcResponse;
    }
    public static RpcResponse forServerErrors(String requestId) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(SERVER_ERRORS);
        rpcResponse.setMessage("SERVER ERRORS");
        return rpcResponse;
    }
    public static RpcResponse forClientErrors(String requestId) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(CLIENT_ERRORS);
        rpcResponse.setMessage("CLIENT ERRORS");
        return rpcResponse;
    }

}
