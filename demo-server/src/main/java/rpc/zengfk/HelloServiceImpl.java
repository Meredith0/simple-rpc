package rpc.zengfk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rpc.zengfk.annotation.RpcService;
import rpc.zengfk.exception.BusinessException;
import rpc.zengfk.service.HelloService;

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
