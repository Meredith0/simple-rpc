package rpc.simple;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.FailStrategy;
import rpc.simple.annotation.MockService;
import rpc.simple.service.HelloService;
import rpc.simple.support.strategy.FailMock;

import java.util.function.BiConsumer;

/**
 * @author zeng.fk
 * 2021-04-20 13:43
 */
@Slf4j
@MockService(name = "helloService")
public class HelloServiceFailmock implements HelloService {

    @Override
    public String sayHello(String str) {
        return null;
    }

    @Override
    public String sayHelloAsync(String str, BiConsumer<?, ?> callback) {
        return null;
    }

    @Override
    public String testBusinessException(String err) {
        return null;
    }

    @Override
    public String testRpcException(String err) {
        log.info("触发服务降级...sayHello:{}", err);
        return "触发服务降级...sayHello" + err;
    }

}
