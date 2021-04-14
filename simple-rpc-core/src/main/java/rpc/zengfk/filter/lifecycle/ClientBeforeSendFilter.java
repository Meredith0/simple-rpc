package rpc.zengfk.filter.lifecycle;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.filter.BreakableFilter;
import rpc.zengfk.model.RpcRequest;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-14 15:32
 */
@Slf4j
public abstract class ClientBeforeSendFilter extends BreakableFilter<RpcRequest, ServiceInstance> {

    /**
     * @param rpcRequest rpc请求
     * @param routedService 路由+负载均衡后选择的serviceInstance
     */
    @Override
    protected Object[] filter(RpcRequest rpcRequest, ServiceInstance routedService) {
        return apply(rpcRequest, routedService, false);
    }
}
