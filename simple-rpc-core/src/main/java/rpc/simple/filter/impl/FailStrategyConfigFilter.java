package rpc.simple.filter.impl;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.RpcFilter;
import rpc.simple.cache.system.FailStrategyCache;
import rpc.simple.filter.lifecycle.ClientBeforeSendFilter;
import rpc.simple.model.Service;
import rpc.simple.model.ServiceInstance;
import rpc.simple.protocol.RpcProtocol;

/**
 * client发送协议前把该接口的容错策略写入协议
 * @author zeng.fk
 * 2021-04-18 23:56
 */
@Slf4j
@RpcFilter
public class FailStrategyConfigFilter extends ClientBeforeSendFilter {

    @Override
    public Object[] doFilter(RpcProtocol rpcProtocol, ServiceInstance serviceInstance) {
        Service service = serviceInstance.getService();
        //将容错策略写入协议
        rpcProtocol.setFailStrategy(FailStrategyCache.getConfig(service).getCode());

        return new Object[]{rpcProtocol, serviceInstance};
    }
}