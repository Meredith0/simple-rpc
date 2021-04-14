package rpc.simple.router;

import rpc.simple.annotation.SPI;
import rpc.simple.model.ServiceInstance;

import java.util.List;

/**
 * @author zeng.fk
 * 2021-04-11 16:55
 */
@SPI
public interface Router {

    ServiceInstance route(List<ServiceInstance> servicePool, Object routeKey);
}
