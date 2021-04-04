package rpc.zengfk.model;
import rpc.zengfk.exception.RpcException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class ServiceInstance {

    public static final String SEPARATOR = "#";
    public String host;
    public String port;
    public String version;
    public String serviceName;

}

