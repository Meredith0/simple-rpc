package rpc.simple;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.RpcService;
import rpc.simple.exception.BusinessException;
import rpc.simple.service.HelloService;

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
    public String testBusinessException(String err) {

        throw new BusinessException(err);
    }
}
