package rpc.simple.router.bypass;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.model.ServiceInstance;
import rpc.simple.router.AbstractRouter;

import java.util.List;

/**
 * @author zeng.fk
 * 2021-04-11 17:13
 */
@Slf4j
public class BypassRouter extends AbstractRouter {

    @Override
    public List<ServiceInstance> doRoute(List<ServiceInstance> servicePool, Object routeKey) {
        return servicePool;
    }
}
