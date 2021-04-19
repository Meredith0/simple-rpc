package rpc.simple.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingletonFactoryTest {

    @Test
    void getInstance() {
        SnowFlakeUtil instance = SingletonFactory.getInstance(SnowFlakeUtil.class, 2, 2);
    }
}