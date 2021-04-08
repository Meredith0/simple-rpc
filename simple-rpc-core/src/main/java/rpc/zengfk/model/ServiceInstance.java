package rpc.zengfk.model;

import lombok.*;
import rpc.zengfk.model.Service;

import java.net.InetSocketAddress;

/**
 * 协议，定义了一个暴露的服务
 *
 * @author zeng.fk
 * 2021-04-01 20:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceInstance extends Service {

    public static final String SEPARATOR = "#";
    public static final String MAGIC_CODE = "aRpc";

    public String host;
    public String port;

    public InetSocketAddress getIp() {
        return new InetSocketAddress(host, Integer.parseInt(port));
    }

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

