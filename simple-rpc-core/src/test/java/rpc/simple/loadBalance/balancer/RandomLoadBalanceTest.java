package rpc.simple.loadBalance.balancer;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.model.ServiceInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class RandomLoadBalanceTest {

    static List<ServiceInstance> services;
    @BeforeAll
    static void setup() {
        ServiceInstance s1 = new ServiceInstance("test1", "1.0.0", "192.168.0.1", "8001");
        ServiceInstance s2 = new ServiceInstance("test2", "1.0.0", "192.168.0.2", "8001");
        ServiceInstance s3=  new ServiceInstance("test3", "1.0.0", "192.168.0.3", "8001");
        services = Lists.newArrayList(s1, s2, s3);
    }
    @Test
    void doSelect() {
        RandomLoadBalance randomLoadBalance = new RandomLoadBalance();
        log.info("selected service is:{}", randomLoadBalance.select(services, ""));
    }

}