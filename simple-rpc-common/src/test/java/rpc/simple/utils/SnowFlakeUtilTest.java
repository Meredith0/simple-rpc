package rpc.simple.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SnowFlakeUtilTest {

    @Test
    void nextId() {
        log.info(SnowFlakeUtil.nextId() + "");
    }
}