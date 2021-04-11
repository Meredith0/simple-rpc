package rpc.zengfk.router;

import rpc.zengfk.annotation.SPI;
import rpc.zengfk.model.ServiceInstance;

import java.util.List;

/**
 * @author zeng.fk
 * 2021-04-11 16:55
 */
@SPI
public interface Router {

    ServiceInstance route(List<ServiceInstance> servicePool, Object routeKey);
}
