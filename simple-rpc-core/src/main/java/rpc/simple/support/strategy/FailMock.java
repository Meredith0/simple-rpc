package rpc.simple.support.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import rpc.simple.annotation.FailStrategy;
import rpc.simple.cache.RpcRequestCache;
import rpc.simple.invoker.ServiceInvoker;
import rpc.simple.model.RpcRequest;
import rpc.simple.model.RpcResponse;
import rpc.simple.protocol.RpcProtocol;
import rpc.simple.provider.ServiceProvider;
import rpc.simple.support.FailTolerate;

/**
 * @author zeng.fk
 * 2021-04-14 10:40
 */
@Slf4j
@FailStrategy
public class FailMock implements FailTolerate {

    @Autowired
    ServiceProvider serviceProvider;


    @Override
    public void process(Object... args) {
        log.info("failmock... {}", args[0]);
        RpcProtocol protocol = (RpcProtocol) args[0];
        RpcResponse response = (RpcResponse) protocol.getBody();
        RpcRequestCache.CacheValue cacheValue = RpcRequestCache.get(response.getRequestId());

        RpcRequest rpcRequest = cacheValue.getRpcRequest();

        Object serviceBean = serviceProvider.get(rpcRequest.getService().getServiceName());
        Object res = ServiceInvoker.getInstance().invoke(rpcRequest, serviceBean);

        response.setData(res);
        response.setCode(RpcResponse.MOCKED);
        log.debug("mocked protocol:{}", protocol);
    }
}
