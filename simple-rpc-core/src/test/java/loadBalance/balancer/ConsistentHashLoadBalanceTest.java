package loadBalance.balancer;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.loadBalance.balancer.ConsistentHashLoadBalance;
import rpc.zengfk.model.ServiceInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class ConsistentHashLoadBalanceTest {
    static List<ServiceInstance> services;
    @BeforeAll
    static void setup() {
        ServiceInstance s1 = ServiceInstance.builder().serviceName("test1").version("1.0.0").host("192.168.0.1").port("1000").build();
        ServiceInstance s2 = ServiceInstance.builder().serviceName("test2").version("1.0.0").host("192.168.0.2").port("1000").build();
        ServiceInstance s3 = ServiceInstance.builder().serviceName("test3").version("1.0.0").host("192.168.0.3").port("1000").build();
        ServiceInstance s4 = ServiceInstance.builder().serviceName("test4").version("1.0.0").host("192.168.0.4").port("1000").build();
        ServiceInstance s5 = ServiceInstance.builder().serviceName("test5").version("1.0.0").host("192.168.0.5").port("1000").build();
        services = Lists.newArrayList(s1, s2, s3, s4, s5);
    }

    @Test
    void doSelect() {
        ConsistentHashLoadBalance consistentHashLoadBalance = new ConsistentHashLoadBalance();
        log.info("the selected service is:{}", consistentHashLoadBalance.select(services, "123"));
        log.info("the selected service is:{}", consistentHashLoadBalance.select(services, "123"));
        log.info("the selected service is:{}", consistentHashLoadBalance.select(services, "456"));
    }

}