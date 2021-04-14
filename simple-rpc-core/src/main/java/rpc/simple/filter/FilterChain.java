package rpc.simple.filter;

import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * @author zeng.fk
 * 2021-04-12 19:28
 */
public class FilterChain {

    Deque<FilterNode> chain;

    public FilterChain() {
        this.chain = new LinkedList<>();
    }

    public void add(FilterNode node) {
        chain.add(node);
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
        Boolean isBreak = false;
        for (FilterNode filterNode : chain) {
            Filter<?, ?> filter = filterNode.getFilter();
            if (filter instanceof BreakableFilter) {
                Object[] res = ((BreakableFilter) filter).apply(args[0], args[1], isBreak);
                args[0] = res[0];
                args[1] = res[1];
                isBreak = (Boolean) res[2];
            }
        }
    }
}
