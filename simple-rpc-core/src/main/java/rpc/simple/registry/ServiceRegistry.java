package rpc.simple.registry;
import rpc.simple.annotation.SPI;
import rpc.simple.model.Metadata;
import rpc.simple.model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-03-30 15:00
 */
@SPI
public interface ServiceRegistry {

    /**
     * 注册服务
     * @param serviceInstance rpc 服务名称
     */
    void register(ServiceInstance serviceInstance);

    /**
     * 注册元数据
     */
    Metadata register(Metadata metadata);

    /**
     * 取消注册服务, 优雅停机时调用
     */
    void unRegister();

}
