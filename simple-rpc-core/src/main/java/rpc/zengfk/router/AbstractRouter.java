package rpc.zengfk.router;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.extension.ExtensionLoader;
import rpc.zengfk.extension.ExtensionName;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.loadBalance.LoadBalance;

import java.net.InetAddress;
import java.util.List;

/**
 * @author zeng.fk
 * 2021-04-11 17:03
 */
@Slf4j
public abstract class AbstractRouter implements Router {

    private final LoadBalance loadBalance;

    public AbstractRouter() {
        this.loadBalance = ExtensionLoader.ofType(LoadBalance .class).getExtension(ExtensionName.LOAD_BALANCE);
    }

    @SneakyThrows
    public ServiceInstance route(List<ServiceInstance> servicePool, Object routeKey) {

        log.debug("进入路由...{}", servicePool);
        List<ServiceInstance> routedPool = doRoute(servicePool, routeKey);
        //一致性哈希负载均衡的key, 默认为本机ip
        String balanceKey = InetAddress.getLocalHost().getHostAddress();
        return loadBalance.select(routedPool, balanceKey);
    }

    protected abstract List<ServiceInstance> doRoute(List<ServiceInstance> servicePool, Object routeKey);

}
