package model;
import exception.RpcException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务实例, 用于服务注册和服务发现
 *
 * @author zeng.fk
 *     2021-04-01 18:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInstance {

    private static final String SEPARATOR = "#";
    public String host;
    public String port;
    public String version;
    public String serviceName;
    public Map<String, String> metadata;

    public String toServicePath() {
        return String.join(SEPARATOR, serviceName, version, host + ":" + port);
    }

    /**
     *  将servicePath解析为ServiceInstance
     */
    public static ServiceInstance parseServicePath(String path) {
        // path: /simple-rpc/provider/{service}#{version}#{host}:{port}
        String[] split = path.split("/");
        if (split.length != 3) {
            throw new RpcException("path 解析异常, path:" + path);
        }
        String[] serviceInstanceStr = split[2].split(SEPARATOR);
        if (serviceInstanceStr.length != 3) {
            throw new RpcException("path 解析异常, path:" + path);
        }
        String[] ipAddress = serviceInstanceStr[2].split(":");

        return ServiceInstance.builder()
                              .serviceName(serviceInstanceStr[0])
                              .version(serviceInstanceStr[1])
                              .host(ipAddress[0])
                              .port(ipAddress[1])
                              .build();

    }
}
