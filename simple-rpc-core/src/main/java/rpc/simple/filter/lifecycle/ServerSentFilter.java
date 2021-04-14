package rpc.simple.filter.lifecycle;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.filter.BreakableFilter;
import rpc.simple.model.RpcResponse;
import rpc.simple.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 23:07
 */
@Slf4j
public abstract class ServerSentFilter extends BreakableFilter<RpcProtocol, RpcResponse> {

    public Object[] filter(RpcProtocol rpcProtocol, RpcResponse rpcResponse) {
        return apply(rpcProtocol, rpcResponse, false);
    }
}
