package rpc.zengfk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import rpc.zengfk.annotation.RpcReference;
import rpc.zengfk.service.HelloService;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zeng.fk
 * 2021-04-06 23:43
 */
@Slf4j
@Component
public class HelloServiceTest {

    @RpcReference(name = "helloService")
    private HelloService helloService;

    void rpcSayHello() {
        String res = helloService.sayHello("foo");
        log.info("============= rpc请求成功, 返回结果{} =============", res);
    }

    @Autowired
    HelloServiceTest self;

    @PostConstruct
    @Bean
    void test() {
        log.info("5秒后发起rpc请求...");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(self::rpcSayHello, 5, TimeUnit.SECONDS);
    }
}
