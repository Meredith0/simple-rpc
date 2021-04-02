package loadBalance.balancer;
import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import model.ServiceInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class RandomLoadBalanceTest {

    static List<ServiceInstance> services;
    @BeforeAll
    static void setup() {
        ServiceInstance s1 = ServiceInstance.builder().serviceName("test1").version("1.0.0").host("192.168.0.1").port("1000").build();
        ServiceInstance s2 = ServiceInstance.builder().serviceName("test2").version("1.0.0").host("192.168.0.2").port("1000").build();
        ServiceInstance s3 = ServiceInstance.builder().serviceName("test3").version("1.0.0").host("192.168.0.3").port("1000").build();
        services = Lists.newArrayList(s1, s2, s3);
    }
    @Test
    void doSelect() {
        RandomLoadBalance randomLoadBalance = new RandomLoadBalance();
        log.info("selected service is:{}", randomLoadBalance.select(services, ""));
    }

}