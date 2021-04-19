package rpc.simple.model;

import lombok.*;

import java.io.Serializable;

/**
 * @author zeng.fk
 * 2021-04-05 16:52
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 691723561234346L;

    private Long requestId;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private Service service;

}
