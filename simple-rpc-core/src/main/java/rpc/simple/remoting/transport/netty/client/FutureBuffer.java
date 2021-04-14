package rpc.simple.remoting.transport.netty.client;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存客户端请求后未被回调的future
 * @author zeng.fk
 * 2021-04-05 11:20
 */
@Slf4j
public final class FutureBuffer {

    /**
     * key: requestId, value:RpcResponse Future
     */
    private static final Map<String, CompletableFuture<RpcResponse>> BUFFER = new ConcurrentHashMap<>();

    public static void put(String requestId, CompletableFuture<RpcResponse> future) {
        BUFFER.put(requestId, future);
    }

    public static void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = BUFFER.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
