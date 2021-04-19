package rpc.simple.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.enums.CacheTypeEnum;
import rpc.simple.model.RpcRequest;
import rpc.simple.model.ServiceInstance;
import rpc.simple.utils.PropertiesUtil;

/**
 * 已发出的请求cache,用于容错
 *
 * @author zeng.fk
 * 2021-04-16 19:22
 */
@Slf4j
public class RpcRequestCache {

    public static final int INIT_CAPACITY = 10;
    public static final long TIMEOUT = Long.parseLong(PropertiesUtil.getTimeoutMillis());
    /**
     * key: requestId, value: RpcRequest
     */
    private static final Cache<CacheValue> CACHE = CacheFactory.newCache(RpcRequestCache.class.getSimpleName(),
        CacheTypeEnum.TIMEOUT, INIT_CAPACITY, TIMEOUT);

    public static void put(Long requestId, RpcRequest rpcRequest, ServiceInstance serviceInstance, Integer retryCount) {

        CacheValue cacheValue = new CacheValue(rpcRequest, serviceInstance, retryCount);
        CACHE.put(requestId, cacheValue);
    }

    public static void put(Long requestId, CacheValue cacheValue) {

        CACHE.put(requestId, cacheValue);
    }

    public static CacheValue get(Long requestId) {
        return CACHE.get(requestId);
    }

    @Data
    @AllArgsConstructor
    public static class CacheValue {

        RpcRequest rpcRequest;
        ServiceInstance lastCalledServiceInstance;
        Integer retryCount;
    }
}

