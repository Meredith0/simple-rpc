package rpc.simple.filter.impl;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.annotation.RpcFilter;
import rpc.simple.cache.FutureCache;
import rpc.simple.exception.BusinessException;
import rpc.simple.exception.RpcException;
import rpc.simple.filter.lifecycle.ClientReceivedFilter;
import rpc.simple.model.RpcResponse;
import rpc.simple.protocol.RpcProtocol;
import rpc.simple.support.FailTolerate;
import rpc.simple.cache.system.FailStrategyCache;
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
        RpcResponse response = (RpcResponse) rpcProtocol.getBody();
        switch (response.getCode()) {
            case RpcResponse.OK:
                break;

            case RpcResponse.CLIENT_ERRORS:
                throw new RpcException("BAD CLIENT");

            case RpcResponse.SERVER_ERRORS:
                handleServerError(rpcProtocol);
                break;

            case RpcResponse.BUSINESS_EXCEPTION:
                handleBusinessException(response);
                break;

            default:
                throw new IllegalStateException();
        }
        return new Object[]{rpcProtocol, channelHandlerContext};
    }

    /**
     * 触发容错机制
     */
    private void handleServerError(RpcProtocol rpcProtocol) {
        //根据协议中指定的容错机制调用容错策略
        byte code = rpcProtocol.getFailStrategy();
        FailTolerate failTolerate = FailStrategyCache.getStrategy(FailStrategyEnum.get(code));

        failTolerate.process(rpcProtocol);
    }

    /**
     * 业务异常不属于错误, 直接抛出
     */
    private void handleBusinessException(RpcResponse response) {
        FutureCache.completeExceptionally(response, new BusinessException(response.toString()));
        throw new BusinessException(response.toString());
    }
}
