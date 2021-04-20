package rpc.simple.filter.impl;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.RpcFilter;
import rpc.simple.filter.lifecycle.ClientSentFilter;
import rpc.simple.model.RpcRequest;
import rpc.simple.model.ServiceInstance;
import rpc.simple.cache.RpcRequestCache;
import rpc.simple.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-16 16:29
 */
@Slf4j
@RpcFilter
public class RpcRequestCacheFilter extends ClientSentFilter {

    @Override
    public Object[] doFilter(RpcProtocol protocol, ServiceInstance serviceInstance) {
        RpcRequest rpcRequest = (RpcRequest) protocol.getBody();
        RpcRequestCache.put(rpcRequest.getRequestId(), rpcRequest, serviceInstance, 0);

        return new Object[]{protocol, serviceInstance};
    }
}
