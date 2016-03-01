import java.util.*;

public class SplayTreeSet<E extends Comparable<? super E>> implements SimpleSet<E> {
    private class Node {
        private E value;
        private Node parent;
        private Node leftChild;
        private Node rightChild;

        public Node(E value) {
            this.value = value;
        }

        public E getValue() {
            return this.value;
        }

        public Node getLeftChild() {
            return this.leftChild;
        }

        public Node getRightChild() {
            return this.rightChild;
        }

        public void setLeftChild(Node node) {
            this.leftChild = node;
            if (node != null) {
                node.parent = this;
            }
        }

        public void setRightChild(Node node) {
            this.rightChild = node;
            if (node != null) {
                node.parent = this;
            }
        }
    }

    private Node root;
    private int size;

    public SplayTreeSet() {
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public boolean add(E x) {
        Node node = findWithoutSplaying(x);

        if (node.getValue().equals(x)) {
            splay(node);
            return false;
        } else {
            Node newNode = new Node(x);
            int comparison = x.compareTo(node.getValue());
            if (comparison < 0) {
                node.setLeftChild(newNode);
            } else if (comparison > 0) {
                node.setRightChild(newNode);
            } else {
                // node and newNode is the same -- should never happen!
                return false;
            }
            splay(newNode);
            size++;
            return true;
        }
    }

    public boolean remove(E x) {
        size--;
        return false;
    }

    public boolean contains(E x) {
        return false;
    }

    /**
     * Finds a node with a specified value in the tree and splays it. If such a
     * node doesn't exist, the node where the search ended is splayed and the
     * method returns null.
     *
     * @param x The value to search for.
     * @return a node from the tree containing the value x. If no such node
     *         exists, null is returned.
     */
    public Node find(E x) {
        Node node = findRecursive(x, this.root);
        splay(node);

        // If we ended up at a node with the specified value, we return the node.
        // Otherwise we return null.
        if (node != null && node.getValue().equals(x)) {
            return node;
        } else {
            return null;
        }
    }

    /**
     * Same as find(E x), but always returns the found node and doesn't splay it.
     */
    private Node findWithoutSplaying(E x) {
        return findRecursive(x, this.root);
    }

    /**
     * Recursively finds a node in the tree containing a specified value. If
     * the specified value is not in the tree, the node where the search ended
     * is returned.
     *
     * @param x The value to search for
     * @param root The root of a subtree to start the search in.
     * @return a node containing the value x if such a node exists, otherwise
     *         the node where the search ended (the last node that wasn't null).
     *         null is returned if root is null.
     */
    private Node findRecursive(E x, Node root) {
        if (root == null) {
            return null;
        }

        int comparison = x.compareTo(root.getValue());

        if (comparison < 0) {
            if (root.getLeftChild() != null) {
                return findRecursive(x, root.getLeftChild());
            }
        } else if (comparison > 0) {
            if (root.getRightChild() != null) {
                return findRecursive(x, root.getRightChild());
            }
        }

        // We have found the right node!
        return root;
    }

    private void splay(Node node) {

    }
}
