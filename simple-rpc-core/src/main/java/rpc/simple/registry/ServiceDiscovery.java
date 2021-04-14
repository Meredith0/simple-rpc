package rpc.simple.registry;
import java.util.List;

import rpc.simple.annotation.SPI;
import rpc.simple.model.Service;
import rpc.simple.model.ServiceInstance;

/**
 *  服务发现
 * @author zeng.fk
 *     2021-03-30 15:04
 */
@SPI
public interface ServiceDiscovery {

    /**
     * 从注册中心查询可供调用的服务地址, 负载均衡在其内部进行
     * @param service 服务名称+版本号
     * @return 服务实例
     */
    List<ServiceInstance> lookup(Service service);
}
