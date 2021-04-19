package rpc.simple.cache;

import org.junit.jupiter.api.Test;
import rpc.simple.model.RpcRequest;

class RpcRequestCacheTest {

    static RpcRequest rpcRequest = new RpcRequest();

    @Test
    void put() {
        RpcRequestCache.put(1L, rpcRequest, null, 0);
    }

    @Test
    void get() {
        RpcRequestCache.CacheValue cacheValue = RpcRequestCache.get(1L);

        assert rpcRequest == cacheValue.getRpcRequest();
    }
}