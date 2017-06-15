package algorithms;

import java.lang.management.ThreadInfo;
import java.util.Stack;

/**
 * Created by evikuzn on 6/15/2017.
 */
public class CombiningTree {
    private Node[] leaf;

    public CombiningTree(int width) {
        Node[] nodes = new Node[width-1];
        nodes[0] = new Node();

        for (int i = 1; i < nodes.length; ++i) {
            nodes[i] = nodes[nodes.length - i - 1];
        }

        leaf = new Node[(width+1)/2];

        for (int i = 0; i < leaf.length; ++i) {
            leaf[i] = nodes[nodes.length-i-1];
        }
    }

    public int getAndIncrement() throws PanicException {
        Stack<Node> stack = new Stack<>();
        final int threadId = (int) Thread.currentThread().getId();
        Node myLeaf = leaf[threadId/2];

        Node node = myLeaf;

        // precombining phase.
        while (node.precombine()) {
            node = node.parent;
        }

        Node stop = node;

        // combining phase.
        node = myLeaf;
        int combined = 1;
        while (node != stop) {
            combined = node.combine(combined);
            stack.push(node);
            node = node.parent;
        }

        // operation phase
        int prior = stop.op(combined);

        //distribution phase
        while (!stack.empty()) {
            node = stack.pop();
            node.distribute(prior);
        }

        return prior;
    }
}
