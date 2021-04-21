package rpc.simple.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zeng.fk
 * 2021-04-05 16:52
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcRequest {

    private Long requestId;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private Service service;
}
