package rpc.zengfk.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zeng.fk
 *     2021-04-04 23:48
 */
@AllArgsConstructor
@Getter
public enum ExtensionNameEnum {

    LOAD_BALANCE("loadBalance"),
    REMOTING("remoting"),
    TRANSPORT("transport"),
    REGISTRY("registry"),
    DISCOVERY("discovery"),
    FILTER("filter"),
    SERIALIZER("serializer"),
    COMPRESSOR("compressor");

    private final String name;

}
