package rpc.zengfk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zeng.fk
 * 2021-04-05 16:20
 */
@AllArgsConstructor
@Getter
public enum FailStrategyEnum {

    FAIL_FAST((byte)0x01),

    FAIL_OVER((byte)0x02),

    FAIL_MOCK((byte)0x03);


    private final byte code;

}
