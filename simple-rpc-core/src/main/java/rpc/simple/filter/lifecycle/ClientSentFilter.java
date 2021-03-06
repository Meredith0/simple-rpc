package rpc.simple.filter.lifecycle;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.filter.BreakableFilter;
import rpc.simple.model.ServiceInstance;
import rpc.simple.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 21:53
 */
@Slf4j
public abstract class ClientSentFilter extends BreakableFilter<RpcProtocol, ServiceInstance> {

    public Object[] filter(RpcProtocol rpcProtocol, ServiceInstance serviceInstance) {
        return apply(rpcProtocol, serviceInstance, false);
    }
}
