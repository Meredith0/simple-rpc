package rpc.simple;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.RpcService;
import rpc.simple.exception.BusinessException;
import rpc.simple.exception.RpcException;
import rpc.simple.service.HelloService;

import java.util.function.BiConsumer;

/**
 * @author zeng.fk
 * 2021-04-06 23:06
 */
@Slf4j
@RpcService(name = "helloService")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String str) {
        log.info("hello {}", str);
        return "hello " + str;
    }

    @Override
    public String sayHelloAsync(String str, BiConsumer<?, ?> callback) {

        log.info("async hello {}", str);
        return "async hello " + str;
    }

    @Override
    public String testBusinessException(String err) {

        throw new BusinessException(err);
    }

    @Override
    public String testRpcException(String err) {
        throw new RpcException(err);
    }
}
