package rpc.zengfk.registry.zookeeper;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import lombok.SneakyThrows;
import rpc.zengfk.model.ServiceInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rpc.zengfk.utils.ZookeeperUtil;

class ZkServiceRegistryTest {

    static ZkServiceRegistry zkServiceRegistry;
    static ServiceInstance serviceInstance;

    @BeforeAll
    static void setup() {
        zkServiceRegistry = new ZkServiceRegistry();
        serviceInstance = new ServiceInstance("testService", "1.0,0", "192.168.0.1", "8080");
    }
    @Test
    @SneakyThrows
    void register() {
        zkServiceRegistry.register(serviceInstance);
        Thread.sleep(100);
        assertTrue(checkPathExist());
    }

    @Test
    @SneakyThrows
    void unRegister() {
        zkServiceRegistry.unRegister();
        assertFalse(checkPathExist());
    }

    @SneakyThrows
    Boolean checkPathExist() {
        Method toServicePath = zkServiceRegistry.getClass().getDeclaredMethod("toServicePath", ServiceInstance.class);
        toServicePath.setAccessible(true);
        String path = (String)toServicePath.invoke(zkServiceRegistry, serviceInstance);
        path = zkServiceRegistry.PATH_PREFIX + serviceInstance.getServiceName() + "/" + path;
        return ZookeeperUtil.getZkClient().checkExists().forPath(path) != null;
    }
}