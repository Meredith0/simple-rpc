package rpc.zengfk.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zeng.fk
 * 2021-04-06 10:14
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Service {
    public String serviceName;
    public String version;
}
