package rpc.simple.support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rpc.simple.support.strategy.Failfast;
import rpc.simple.support.strategy.FailMock;
import rpc.simple.support.strategy.Failover;

/**
 * @author zeng.fk
 * 2021-04-05 16:20
 */
@AllArgsConstructor
@Getter
public enum FailStrategyEnum {
    FAIL_FAST((byte) 0x01, Failfast.class),
    FAIL_OVER((byte) 0x02, Failover.class),
    FAIL_MOCK((byte) 0x03, FailMock.class),
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

    public static FailStrategyEnum of(byte code) {
        for (FailStrategyEnum f : FailStrategyEnum.values()) {
            if (f.getCode() == code) {
                return f;
            }
        }
        return null;
    }
}
