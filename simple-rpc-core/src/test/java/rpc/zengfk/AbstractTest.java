package rpc.zengfk;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rpc.zengfk.model.Service;
import rpc.zengfk.model.ServiceInstance;

import java.util.List;

/**
 * @author zeng.fk
 * 2021-04-06 10:39
 */
@Slf4j
public abstract class AbstractTest {
    public static List<ServiceInstance> serviceInstances;

    @BeforeAll
    protected static void baseSetup() {
        ServiceInstance s1 = new ServiceInstance("test1", "1.0.0", "192.168.0.1", "8001");
        ServiceInstance s2 = new ServiceInstance("test1", "1.0.0", "192.168.0.2", "8001");
        ServiceInstance s3=  new ServiceInstance("test1", "1.0.0", "192.168.0.3", "8001");
        ServiceInstance s4 = new ServiceInstance("test1", "1.0.0", "192.168.0.4", "8001");
        ServiceInstance s5 = new ServiceInstance("test1", "1.0.0", "192.168.0.5", "8001");
        serviceInstances = Lists.newArrayList(s1, s2, s3, s4, s5);
    }
}
