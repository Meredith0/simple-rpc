package rpc.simple;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import rpc.simple.annotation.RpcReference;
import rpc.simple.service.HelloService;
import rpc.simple.support.enums.FailStrategyEnum;

import javax.annotation.PostConstruct;
import java.util.Scanner;
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

    @RpcReference(name = "helloService", failStrategy = FailStrategyEnum.FAIL_OVER)
    private HelloService helloService;
    @RpcReference(name = "helloService", failStrategy = FailStrategyEnum.FAIL_MOCK)
    private HelloService failMockTest;

    @Autowired
    HelloServiceTest self;

    public HelloServiceTest() {
    }

    private void rpcSayHello() {
        String res = helloService.sayHello("foo");
        log.info("============= 测试正常rpc调用, 返回结果: {} =============", res);
    }

    private void rpcSayHelloAsync() {
        String res = helloService.sayHelloAsync("foo",(o,o1)->{
            log.info("{},{}", o, o1);
        });
    }

    private void testBusinessException() {
        String res = helloService.testBusinessException("business exception occurs...");
        log.info("============= 测试业务异常, 返回结果: {} =============", res);
    }

    private void testRpcException() {
        String res = helloService.testRpcException("rpc exception occurs...");
        log.info("============= 测试rpc异常, 返回结果: {} =============", res);
    }

    private void testFailMock() {
        String res = failMockTest.testRpcException("fail mock");
        log.info("============= 测试fail mock, 返回结果: {} =============",res);
    }

    @PostConstruct
    @Bean
    void test() {
        log.info("5秒后发起rpc请求...");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(self::eventLoop, 5, TimeUnit.SECONDS);
    }

    @SneakyThrows
    void eventLoop() {
        while (true) {
            log.info("*************1: 测试正常rpc调用 *************");
            log.info("*************2: 测试业务异常 *************");
            log.info("*************3: 测试rpc异常 *************");
            log.info("*************4: 测试FailMock *************");
            log.info("请输入:");
            Scanner scanner = new Scanner(System.in);
            int read = scanner.nextInt();

            switch (read) {
                case 1:
                    self.rpcSayHello();
                    break;
                case 2:
                    self.testBusinessException();
                    break;
                case 3:
                    self.testRpcException();
                    break;
                case 4:
                    self.testFailMock();
                    break;

                default:
                    throw new IllegalStateException();
            }
        }
    }
}
