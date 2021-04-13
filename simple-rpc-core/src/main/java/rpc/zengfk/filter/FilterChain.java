package rpc.zengfk.filter;

import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * @author zeng.fk
 * 2021-04-12 19:28
 */
public class FilterChain<T> {

    Deque<FilterNode> chain;

    public FilterChain() {
        this.chain = new LinkedList<>();
    }

    public void add(FilterNode node) {
        if (node != null) {
            chain.add(node);
        }
        sort();
    }

    /**
     * 根据节点的权重排序, 从大到小
     */
    private void sort() {
        chain = chain.stream()
            .sorted(Comparator.comparingInt(FilterNode::getPriority).reversed())
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public void invokeChain(Object... args) {
        chain.forEach(
            filterNode -> {
                Filter<?, ?> filter = filterNode.getFilter();
                if (filter instanceof BreakableFilter) {
                    Object[] res = ((BreakableFilter) filter).apply(args[0], args[1]);
                    args[0] = res[0];
                    args[1] = res[1];
                }
            }
        );
    }
}
