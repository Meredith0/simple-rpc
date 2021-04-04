package registry.zookeeper;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import lombok.SneakyThrows;
import rpc.zengfk.model.ServiceInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rpc.zengfk.registry.zookeeper.ZkServiceRegistry;
import rpc.zengfk.utils.ZookeeperUtil;

class ZkServiceRegistryTest {

    static ZkServiceRegistry zkServiceRegistry;
    static ServiceInstance serviceInstance;

    @BeforeAll
    static void setup() {
        zkServiceRegistry = new ZkServiceRegistry();
        serviceInstance = ServiceInstance.builder()
                                         .serviceName("testService")
                                         .version("1.0.0")
                                         .host("127.0.0.1")
                                         .port("8080")
                                         .build();
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