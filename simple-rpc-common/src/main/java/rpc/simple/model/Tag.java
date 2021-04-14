package rpc.simple.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zeng.fk
 * 2021-04-11 17:15
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Tag {

    protected String name;
}
