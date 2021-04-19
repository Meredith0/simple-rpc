package rpc.simple.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 定义了一个服务
 * @author zeng.fk
 * 2021-04-05 10:14
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Service {

    protected String serviceName;
    protected String version;
    @EqualsAndHashCode.Exclude
    protected Tag tag;

    public Service(String serviceName, String version) {
        this.serviceName = serviceName;
        this.version = version;
    }
}
