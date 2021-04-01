package registry;
import java.net.InetSocketAddress;
import model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-03-30 15:00
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     * @param rpcServiceName rpc 服务名称
     * @param rpcServerAddress rpc 服务地址
     */
    void register(ServiceInstance serviceInstance);

    /**
     * 取消注册服务, 优雅停机时调用
     */
    void unRegister(ServiceInstance serviceInstance);

}
