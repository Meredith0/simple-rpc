package rpc.zengfk.filter;

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

    public FilterNode(Filter filter, int priority) {
        this.filter = filter;
        this.setPriority(priority);
    }

    public void setPriority(int priority) {
        this.priority = normalize(priority);
    }

    private int normalize(int priority) {
        if (priority < 0) {
            priority = 0;
        } else if (priority > 10) {
            priority = 10;
        }
        return priority;
    }
}
