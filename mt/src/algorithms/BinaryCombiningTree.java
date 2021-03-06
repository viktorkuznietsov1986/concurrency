package algorithms;

import java.lang.management.ThreadInfo;
import java.util.Stack;

/**
 * Created by evikuzn on 6/15/2017.
 */
public class BinaryCombiningTree implements CombiningTree {

    public static class Node {
        enum CStatus {IDLE, FIRST, SECOND, RESULT, ROOT};
        boolean locked;
        CStatus status;
        int firstValue, secondValue;
        int result;
        Node parent;

        public Node() {
            status = CStatus.ROOT;
            locked = false;
        }

        public Node(Node parent) {
            this.parent = parent;
            status = CStatus.IDLE;
            locked = false;
        }

        /**
         * Prepares the node to be combined. Moves the node states to a proper values considering the initial state.
         * @return true if the status is not final, false otherwise.
         * @throws PanicException
         */
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
                    locked = true;
                    status = CStatus.SECOND;
                    return false;

                case ROOT:

                    return false;

                default:
                    throw new PanicException("unexpected Node state " + status);
            }
        }

        /**
         * Represents the class combining phase. Applies addition to firstValue and secondValue.
         * @param combined value to apply to store in firstValue.
         * @return the result of combining.
         * @throws PanicException
         */
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

                default:
                    throw new PanicException("unexpected Node state " + status);
            }
        }

        /**
         * Apply the operation.
         * @param combined value to be added or to be applied for combination.
         * @return
         * @throws PanicException
         */
        synchronized int op(int combined) throws PanicException {
            switch (status) {
                case ROOT:
                    int prior = result;
                    result += combined;
                    return prior;

                case SECOND:
                    secondValue = combined;
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
                    throw new PanicException("node state is unexpected " + status);

            }
        }

        /**
         * Distribution phase.
         * @param prior value to be applied for the case if the status is SECOND.
         * @throws PanicException
         */
        synchronized void distribute(int prior) throws PanicException {
            switch (status) {
                case FIRST:
                    status = CStatus.IDLE;
                    locked = false;
                    break;

                case SECOND:
                    result = prior + firstValue;
                    status = CStatus.RESULT;
                    break;

                default:
                    throw new PanicException("unexpected Node state " + status);
            }

            notifyAll();
        }
    }


    private Node[] leaf;

    public BinaryCombiningTree(int width) {
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
