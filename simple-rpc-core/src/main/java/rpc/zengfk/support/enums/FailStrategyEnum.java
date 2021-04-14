package rpc.zengfk.support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rpc.zengfk.support.strategy.Failfast;
import rpc.zengfk.support.strategy.Failmock;
import rpc.zengfk.support.strategy.Failover;

/**
 * @author zeng.fk
 * 2021-04-05 16:20
 */
@AllArgsConstructor
@Getter
public enum FailStrategyEnum {
    FAIL_FAST((byte) 0x01, Failfast.class),
    FAIL_OVER((byte) 0x02, Failover.class),
    FAIL_MOCK((byte) 0x03, Failmock.class),
    ;
    private final byte code;
    private final Class<?> clazz;

    public static Class<?> get(byte code) {
        for (FailStrategyEnum c : FailStrategyEnum.values()) {
            if (c.getCode() == code) {
                return c.clazz;
            }
        }
        return null;
    }
}
