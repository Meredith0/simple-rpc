package rpc.zengfk.model;

import lombok.*;
import rpc.zengfk.protocol.RpcProtocol;

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

    private String requestId;

    private Integer code;

    private String message;

    private Object data;

    public static RpcResponse forSuccess(String requestId, Object data) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setData(data);
        rpcResponse.setCode(200);
        rpcResponse.setMessage("success");
        return rpcResponse;
    }
    public static RpcResponse forFail(String requestId) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(500);
        rpcResponse.setMessage("fail");
        return rpcResponse;
    }

}
