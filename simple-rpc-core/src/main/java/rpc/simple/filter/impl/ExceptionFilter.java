package rpc.simple.filter.impl;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.RpcFilter;
import rpc.simple.exception.BusinessException;
import rpc.simple.exception.RpcException;
import rpc.simple.filter.lifecycle.ClientReceivedFilter;
import rpc.simple.model.RpcResponse;
import rpc.simple.protocol.RpcProtocol;
import rpc.simple.support.FailStrategy;
import rpc.simple.support.FailStrategyCache;
import rpc.simple.support.enums.FailStrategyEnum;

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
