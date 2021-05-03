package rpc.simple.service;

import java.util.function.BiConsumer;

/**
 * @author zeng.fk
 * 2021-04-06 23:06
 */
public interface HelloService {

    String sayHello(String str);

    String testBusinessException(String err);

    String testRpcException(String err);
}
