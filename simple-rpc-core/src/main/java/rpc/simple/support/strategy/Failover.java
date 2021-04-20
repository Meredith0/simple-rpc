package rpc.simple.support.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import rpc.simple.annotation.FailStrategy;
import rpc.simple.cache.RpcRequestCache;
import rpc.simple.extension.ExtensionLoader;
import rpc.simple.extension.ExtensionName;
import rpc.simple.model.RpcResponse;
import rpc.simple.model.ServiceInstance;
import rpc.simple.protocol.RpcProtocol;
import rpc.simple.registry.ServiceDiscovery;
import rpc.simple.remoting.transport.RpcTransport;
import rpc.simple.router.Router;
import rpc.simple.support.AbstractFailStrategy;
import rpc.simple.utils.SnowFlakeUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author zeng.fk
 * 2021-04-14 10:38
 */
@Slf4j
@FailStrategy
public class Failover extends AbstractFailStrategy {

    @Autowired
    private RpcTransport transport;

    @Override
    public Object process(Object... args) {
        return retry(args[0]);
    }

    private Object retry(Object p) {
        log.info("failover retry {}", p);
        RpcProtocol protocol = (RpcProtocol) p;
        RpcResponse response = (RpcResponse) protocol.getBody();
        RpcRequestCache.CacheValue cacheValue = RpcRequestCache.get(response.getRequestId());

        ServiceDiscovery serviceDiscovery = ExtensionLoader.ofType(ServiceDiscovery.class).getExtension(ExtensionName.DISCOVERY);
        Router router = ExtensionLoader.ofType(Router.class).getExtension(ExtensionName.ROUTER);

        ServiceInstance lastServiceInstance = cacheValue.getLastCalledServiceInstance();

        List<ServiceInstance> lookup = serviceDiscovery.lookup(lastServiceInstance.getService());
        //尽量不调用上次失败的那个服务
        if (lookup.size() > 1) {
            lookup.remove(lastServiceInstance);
        }

        ServiceInstance routedService = router.route(lookup, lastServiceInstance.getService().getTag());

        //重新生成requestId
        cacheValue.getRpcRequest().setRequestId(SnowFlakeUtil.nextId());
        //发送请求
        CompletableFuture<RpcResponse> future =
            (CompletableFuture<RpcResponse>) transport.sendAsync(cacheValue.getRpcRequest(), routedService);

        try {
            //FIXME 收不到回调
            RpcResponse rpcResponse = future.get();
            protocol.setBody(rpcResponse);
        } catch (InterruptedException | ExecutionException e) {
            log.error("retry failed...");
            e.printStackTrace();
        }

        return protocol;
    }
}
