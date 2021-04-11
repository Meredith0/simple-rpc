package rpc.zengfk.router.bypass;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.router.AbstractRouter;

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
