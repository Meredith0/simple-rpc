package rpc.zengfk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.router.tag.model.Tag;

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

    public String serviceName;
    public String version;
    @EqualsAndHashCode.Exclude
    public Tag tag;

    public Service(String serviceName, String version) {
        this.serviceName = serviceName;
        this.version = version;
    }
}
