package rpc.simple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zeng.fk
 * 2021-04-05 15:32
 */
@Slf4j
@AllArgsConstructor
@Getter
public enum SerializerEnum {

    PROTOSTUFF((byte) 0x01, "protostuff");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializerEnum c : SerializerEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
