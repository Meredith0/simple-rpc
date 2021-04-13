package rpc.zengfk.filter.lifecycle;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.filter.BreakableFilter;
import rpc.zengfk.model.ServiceInstance;
import rpc.zengfk.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 21:53
 */
@Slf4j
public abstract class ClientSentFilter extends BreakableFilter<RpcProtocol, ServiceInstance> {

    public Object[] filter(RpcProtocol rpcProtocol, ServiceInstance serviceInstance) {
        return apply(rpcProtocol, serviceInstance);
    }
}
