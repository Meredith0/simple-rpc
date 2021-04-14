package rpc.simple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zeng.fk
 * 2021-04-06 21:02
 */
@AllArgsConstructor
@Getter
public enum CompressorEnum {
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressorEnum c : CompressorEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
