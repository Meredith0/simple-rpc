package rpc.zengfk.model;

import lombok.*;

/**
 * 服务实例, 用于服务注册和服务发现
 *
 * @author zeng.fk
 * 2021-04-01 18:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceInstance extends Service {

    public static final String SEPARATOR = "#";
    public String host;
    public String port;

    public ServiceInstance(String serviceName, String version, String host, String port) {
        super(serviceName, version);
        this.host = host;
        this.port = port;
    }

    public ServiceInstance(Service service, String host, String port) {
        super(service.getServiceName(), service.version);
        this.host = host;
        this.port = port;
    }
}

