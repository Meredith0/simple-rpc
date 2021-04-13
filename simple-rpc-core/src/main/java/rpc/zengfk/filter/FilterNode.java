package rpc.zengfk.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zeng.fk
 * 2021-04-12 19:31
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterNode {

    private static final int DEFAULT_PRIORITY = 4;
    /**
     * 过滤器节点
     */
    private Filter filter;
    /**
     * 优先级,从0-10,默认4
     */
    private int priority;

    public FilterNode(Filter filter) {
        this.filter = filter;
        this.setPriority(DEFAULT_PRIORITY);
    }

    public void setPriority(int priority) {
        if (priority < 0 || priority > 10) {
            throw new IllegalArgumentException("priority必须在0-10之间");
        }
        this.priority = priority;
    }
}
