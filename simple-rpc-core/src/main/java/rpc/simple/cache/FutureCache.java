package rpc.simple.cache;

import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.enums.CacheTypeEnum;
import rpc.simple.model.RpcResponse;
import rpc.simple.utils.PropertiesUtil;

import java.util.concurrent.CompletableFuture;

/**
 * 缓存客户端请求后未被回调的future
 *
 * @author zeng.fk
 * 2021-04-05 11:20
 */
@Slf4j
public final class FutureCache {

    private static final int INIT_CAPACITY = 64;
    public static final long TIMEOUT = Long.parseLong(PropertiesUtil.getTimeoutMillis());
    /**
     * key: requestId, value:RpcResponse Future
     */
    private static final Cache<Long,  CompletableFuture<RpcResponse>> CACHE = CacheFactory.newCache(FutureCache.class.getSimpleName(), CacheTypeEnum.TIMEOUT, INIT_CAPACITY, TIMEOUT);

    public static void put(Long requestId, CompletableFuture<RpcResponse> future) {
        CACHE.put(requestId, future);
    }

    public static void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = CACHE.getIfPresent(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
            return;
        }
        throw new IllegalStateException();
    }

    public static void completeExceptionally(RpcResponse rpcResponse, Exception e) {
        CompletableFuture<RpcResponse> future = CACHE.getIfPresent(rpcResponse.getRequestId());
        if (future != null) {
            future.completeExceptionally(e);
            CACHE.invalidate(rpcResponse.getRequestId());
            return;
        }
        throw new IllegalStateException();
    }
}
