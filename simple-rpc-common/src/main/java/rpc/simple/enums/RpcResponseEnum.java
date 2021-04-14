package rpc.simple.enums;

import lombok.*;

/**
 * @author zeng.fk
 * 2021-04-05 17:26
 */
@AllArgsConstructor
@ToString
@Getter
public enum RpcResponseEnum {

    SUCCESS(200),
    FAIL(500);

    private final Integer code;
}
