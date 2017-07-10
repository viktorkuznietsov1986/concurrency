package algorithms;

import java.util.Stack;

/**
 * Created by evikuzn on 7/10/2017.
 */
public class TernaryCombiningTree implements CombiningTree {

    static class Node {
        enum CStatus { IDLE, FIRST, SECOND, THIRD, RESULT, ROOT }

        boolean locked = false;
        CStatus status;
        int firstValue, secondValue, thirdValue;
        int result;
        Node parent;

        public Node() {
            this.status = CStatus.ROOT;
        }

        public Node(Node parent) {
            this.parent = parent;
            this.status = CStatus.IDLE;
        }

        synchronized boolean precombine() throws PanicException {
            while (locked) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            switch (status) {
                case IDLE:
                    status = CStatus.FIRST;
                    return true;

                case FIRST:
                    status = CStatus.SECOND;
                    return true;

                case SECOND:
                    locked = true;
                    status = CStatus.THIRD;
                    return false;

                case ROOT:
                    return false;

                default:
                    throw new PanicException("unexpected Node state " + status);

            }
        }

        synchronized int combine(int combined) throws PanicException {
            while (locked) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            locked = true;
            firstValue = combined;

            switch (status) {
                case FIRST:
                    return firstValue;
                case SECOND:
                    return firstValue + secondValue;

                case THIRD:
                    return firstValue + secondValue + thirdValue;

                default:
                    throw new PanicException("unexpected node state " + status);
            }
        }

        synchronized int op(int combined) throws PanicException {
            switch (status) {
                case ROOT:
                    int prior = result;
                    result += combined;
                    return prior;

                case THIRD:
                    thirdValue = combined;
                    locked = false;
                    notifyAll();

                    while (status != CStatus.RESULT) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    locked = false;
                    notifyAll();
                    status = CStatus.IDLE;
                    return result;

                default:
                    throw new PanicException("incorrect node state " + status);
            }
        }

        synchronized void distribute(int prior) throws PanicException {
            switch (status) {
                case FIRST:
                    status = CStatus.IDLE;
                    locked = false;
                    break;

                case SECOND:
                    secondValue = prior + firstValue;
                    status = CStatus.THIRD;
                    break;

                case THIRD:
                    result = prior + firstValue + secondValue;
                    status = CStatus.RESULT;
                    break;

                default:
                    throw new PanicException("unexpected node state " + status);
            }
        }
    }

    private Node[] leaf;

    public TernaryCombiningTree(int width) {
        Node[] nodes = new Node[width-1];
        nodes[0] = new Node();

        for (int i = 1; i < nodes.length; ++i) {
            nodes[i] = new Node(nodes[(i-1)/3]);
        }

        leaf = new Node[(width+1)/3];

        for (int i = 0; i < leaf.length; ++i) {
            leaf[i] = nodes[nodes.length-i-1];
        }
    }

    /**
     * Gets the current value and increments.
     *
     * @return the value currently stored in the root element.
     * @throws PanicException
     */
    @Override
    public int getAndIncrement() throws PanicException {
        Stack<Node> stack = new Stack<Node>();
        final int threadId = (int) Thread.currentThread().getId();
        Node myLeaf = leaf[threadId/3];

        Node node = myLeaf;

        while (node.precombine()) {
            node = node.parent;
        }

        Node stop = node;
        node = myLeaf;
        int combined = 1;

        while (node != stop) {
            combined = node.combine(combined);
            stack.push(node);
            node = node.parent;
        }

        int prior = stop.op(combined);

        while (!stack.empty()) {
            node = stack.pop();
            node.distribute(prior);
        }

        return combined;
    }
}
