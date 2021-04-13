package rpc.zengfk.filter.lifecycle;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.filter.BreakableFilter;
import rpc.zengfk.model.RpcResponse;
import rpc.zengfk.protocol.RpcProtocol;

/**
 * @author zeng.fk
 * 2021-04-12 23:07
 */
@Slf4j
public abstract class ServerSentFilter extends BreakableFilter<RpcProtocol, RpcResponse> {

    public Object[] filter(RpcProtocol rpcProtocol, RpcResponse rpcResponse) {
        return apply(rpcProtocol, rpcResponse);
    }
}
