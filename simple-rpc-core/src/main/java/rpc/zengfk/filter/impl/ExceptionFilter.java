package rpc.zengfk.filter.impl;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.annotation.RpcFilter;
import rpc.zengfk.exception.BusinessException;
import rpc.zengfk.exception.RpcException;
import rpc.zengfk.filter.lifecycle.ClientReceivedFilter;
import rpc.zengfk.model.RpcResponse;
import rpc.zengfk.protocol.RpcProtocol;
import rpc.zengfk.support.FailStrategy;
import rpc.zengfk.support.FailStrategyCache;
import rpc.zengfk.support.enums.FailStrategyEnum;

/**
 * @author zeng.fk
 * 2021-04-12 23:52
 */
@Slf4j
@RpcFilter
public class ExceptionFilter extends ClientReceivedFilter {

    @Override
    public Object[] doFilter(RpcProtocol rpcProtocol, ChannelHandlerContext channelHandlerContext) {
        RpcResponse response = (RpcResponse) rpcProtocol.getData();
        switch (response.getCode()) {
            case RpcResponse.OK:
                break;

            case RpcResponse.CLIENT_ERRORS:
                throw new RpcException("BAD CLIENT");

            case RpcResponse.SERVER_ERRORS:
                doFailStrategy(rpcProtocol);

            case RpcResponse.BUSINESS_EXCEPTION:
                throw new BusinessException(response.toString());

            default:
                throw new IllegalStateException();
        }
        return new Object[]{rpcProtocol, channelHandlerContext};
    }

    private void doFailStrategy(RpcProtocol rpcProtocol) {
        byte code = rpcProtocol.getFailStrategy();
        FailStrategy failStrategy = FailStrategyCache.get(FailStrategyEnum.get(code));
        failStrategy.process(rpcProtocol);
    }

    private Object handleBusinessException(RpcResponse response) {
        return null;
    }
}
