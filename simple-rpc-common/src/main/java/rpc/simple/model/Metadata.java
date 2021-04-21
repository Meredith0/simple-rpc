package rpc.simple.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务元数据, 目前仅用于雪花算法
 * @author zeng.fk
 * 2021-04-16 17:10
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {

    private static Metadata METADATA_INSTANCE = null;

    public static Metadata get() {
        assert METADATA_INSTANCE != null;
        return METADATA_INSTANCE;
    }

    public static void register(Metadata metadata) {
        METADATA_INSTANCE = metadata;
    }

    Integer datacenterId;
    Integer workerId;
}
