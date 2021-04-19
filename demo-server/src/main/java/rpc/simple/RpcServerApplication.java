package rpc.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zeng.fk
 * 2021-04-06 22:57
 */
@Slf4j
@SpringBootApplication
@ComponentScan(value = {"rpc.simple.support.strategy", "rpc.*"})
public class RpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
    }
}
