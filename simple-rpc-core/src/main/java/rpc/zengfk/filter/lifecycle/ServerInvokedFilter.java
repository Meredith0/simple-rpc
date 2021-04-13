package rpc.zengfk.filter.lifecycle;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.filter.BreakableFilter;
import rpc.zengfk.model.RpcRequest;

/**
 * @author zeng.fk
 * 2021-04-12 21:04
 */
@Slf4j
public abstract class ServerInvokedFilter extends BreakableFilter<RpcRequest, Object> {

    @Override
    protected Object[] filter(RpcRequest rpcRequest, Object serviceBean) {
        return apply(rpcRequest, serviceBean);
    }
}
