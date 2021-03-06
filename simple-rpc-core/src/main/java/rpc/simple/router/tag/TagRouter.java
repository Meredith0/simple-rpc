package rpc.simple.router.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import rpc.simple.model.ServiceInstance;
import rpc.simple.router.AbstractRouter;
import rpc.simple.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zeng.fk
 * 2021-04-11 17:12
 */
@Slf4j
public class TagRouter extends AbstractRouter {

    @Override
    public List<ServiceInstance> doRoute(List<ServiceInstance> servicePool, Object routeKey) {
        log.debug("调用TagRouter...{}", routeKey);
        if (routeKey == null) {
            return servicePool;
        }
        if (routeKey instanceof Tag) {
            if (StringUtils.isEmpty(((Tag)routeKey).getName())) {
                return servicePool;
            }
        }
        return servicePool.stream()
            .filter(service -> service.getTag().equals(routeKey))
            .collect(Collectors.toList());
    }
}
