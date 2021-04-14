package rpc.simple.registry.zookeeper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rpc.simple.AbstractTest;
import rpc.simple.model.Service;
import rpc.simple.model.ServiceInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ZkServiceDiscoveryTest extends AbstractTest {

    @BeforeAll
    static void setup() {
        ZkServiceRegistry zkServiceRegistry = new ZkServiceRegistry();
        serviceInstances.forEach(zkServiceRegistry::register);
    }
    @Test
    @SneakyThrows
    void lookup() {
        ZkServiceDiscovery zkServiceDiscovery = new ZkServiceDiscovery();
        List<ServiceInstance> fromZk = zkServiceDiscovery.lookup(new Service("test1", "1.0.0"));
        assertFalse(fromZk.isEmpty());
        log.info("测试第一次从zk读取:{}", fromZk);

        List<ServiceInstance> fromCache = zkServiceDiscovery.lookup(new Service("test1", "1.0.0"));
        log.info("测试第二次从缓存读取成功! {}", fromCache);

        Thread.sleep(5000);
        log.info("zk手动下线一个服务, 测试childWatch! {}", fromCache);
        List<ServiceInstance> test3 = zkServiceDiscovery.lookup(new Service("test1", "1.0.0"));
        log.info("下线一个服务后 {}", test3);

    }


}