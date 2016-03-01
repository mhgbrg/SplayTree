import java.util.*;

/**
 * A Splay Tree that doesn't allow duplicate values. The behavoiur of the tree
 * is based on the description in https://www.cs.berkeley.edu/~jrs/61b/lec/36.
 */
public class SplayTreeSet<E extends Comparable<? super E>> implements SimpleSet<E> {
    /**
     * A node in the Splay Tree. All the heavy work in the Splay Tree is done
     * in this class. It has recursive methods for doing the ordinary operations.
     */
    private class Node {
        /**
         * Objects of this type is returned by the methods in the Node class.
         * This is because as a caller I want to know both if the operation
         * succeeded or not, and new node of the tree.
         */
        public class SplayResult {
            private boolean result;
            private Node newRoot;

            public SplayResult(boolean result, Node newRoot) {
                this.result = result;
                this.newRoot = newRoot;
            }

            public boolean getResult() {
                return this.result;
            }

            public Node getNewRoot() {
                return this.newRoot;
            }
        }

        private E value;
        private Node parent;
        private Node leftChild;
        private Node rightChild;

        public Node(E value) {
            this.value = value;
        }

        /**
         * Determines whether or not this node is the left child of its parent.
         *
         * @return true if this node is a left child, otherwise false. If this
         *         node doesn't have a parent (it is the root), this method
         *         will return false.
         */
        private boolean isLeftChild() {
            return this.parent != null && this.parent.leftChild == this;
        }

        /**
         * Determines whether or not this node is the right child of its parent.
         * @return true if this node is a right child, otherwise false. If this
         *         node doesn't have a parent (it is the root), this method
         *         will return false.
         */
        private boolean isRightChild() {
            return this.parent != null && this.parent.rightChild == this;
        }

        /**
         * Sets the left child of this node to a new node. This will also update
         * the specified node's parent reference.
         *
         * @param node The new child node.
         */
        private void setLeftChild(Node node) {
            this.leftChild = node;
            if (node != null) {
                node.parent = this;
            }
        }

        /**
         * Sets the right child of this node to a new node. This will also update
         * the specified node's parent reference.
         *
         * @param node The new child node.
         */
        private void setRightChild(Node node) {
            this.rightChild = node;
            if (node != null) {
                node.parent = this;
            }
        }

        /**
         * Replaces this node as a child with a new node. This will set the
         * correct references depending on whether this node is a left or right
         * child of it's parent. This node's parent reference will be set to null.
         *
         * @param node the node to replace this node with.
         */
        private void replaceWith(Node node) {
            if (this.parent != null) {
                if (this == this.parent.leftChild) {
                    this.parent.setLeftChild(node);
                } else if (this == this.parent.rightChild) {
                    this.parent.setRightChild(node);
                }
            } else {
                if (node != null) {
                    node.parent = null;
                }
            }
        }

        /**
         * Finds a value in the tree and returns the node containing that value.
         * If no such node exists, the last node visited by the search will be
         * returned (essentially the last non-null node).
         *
         * @param x the value to find.
         * @return the node containing the specified value. If no such node exists,
         * the last node visited by the search will be returned (essentially the
         * last non-null node).
         */
        private Node find(E x) {
            int comparison = x.compareTo(this.value);

            if (comparison < 0) {
                if (this.leftChild != null) {
                    return this.leftChild.find(x);
                }
            } else if (comparison > 0) {
                if (this.rightChild != null) {
                    return this.rightChild.find(x);
                }
            }

            // We have found the right node!
            return this;
        }

        /**
         * Finds the maximum value in a subtree. The search starts at this
         * node and continues downwards. This is done by continously following
         * the right child reference until a null value is found.
         *
         * @return the node with the maximum value below this node.
         */
        private Node findMax() {
            if (this.rightChild == null) {
                return this;
            } else {
                return this.rightChild.findMax();
            }
        }

        /**
         * Adds a value to the tree. If the value is indeed inserted (it is not
         * present in the tree since before) the newly inserted node will be
         * splayed. If the value already existed in the tree the old node containing
         * that value will be splayed.
         *
         * @param x The value to add.
         * @return a SplayResult with the result of the add (true if the value
         *         was inserted, false if it was already present in the tree) and
         *         the node that was splayed.
         */
        public SplayResult add(E x) {
            Node node = this.find(x);

            if (node.value.equals(x)) {
                node.splay();
                return new SplayResult(false, node);
            } else {
                int comparison = x.compareTo(node.value);
                Node newNode = new Node(x);
                if (comparison < 0) {
                    node.setLeftChild(newNode);
                } else if (comparison > 0) {
                    node.setRightChild(newNode);
                }
                newNode.splay();
                return new SplayResult(true, newNode);
            }
        }

        /**
         * Removes a value from the tree. If the value was removed the parent of
         * the maximum value in the nodes left subtree will be splayed. If the
         * value doesn't exist in the three, the last node visited by the search
         * will be splayed (essentially the last non-null node).
         *
         * @param x The value to remove.
         * @return a SplayResult with the result of the remove (true if the value
         *         was removed, false if it wasn't present in the tree) and the
         *         node that was splayed.
         */
        public SplayResult remove(E x) {
            Node node = this.find(x);

            if (!node.value.equals(x)) {
                node.splay();
                return new SplayResult(false, node);
            } else {
                Node splayNode = node.remove();
                if (splayNode != null) {
                    splayNode.splay();
                }
                return new SplayResult(true, splayNode);
            }
        }

        /**
         * Actually removes a node from the tree. If the node has no children,
         * the node is simply removed. If it has one child that child will take
         * it's place in the tree. If it has two children, the maximum node in
         * the left subtree will be deleted and it's value will be copied to this
         * node.
         *
         * @return the node that is the new root of the tree.
         */
        private Node remove() {
            if (this.leftChild == null && this.rightChild == null) {
                this.replaceWith(null);
                return parent;
            } else if (this.leftChild == null) {
                this.replaceWith(this.rightChild);

                if (parent != null) {
                    return parent;
                } else {
                    return this.rightChild;
                }
            } else if (this.rightChild == null) {
                this.replaceWith(this.leftChild);

                if (parent != null) {
                    return parent;
                } else {
                    return this.leftChild;
                }
            } else {
                Node max = this.leftChild.findMax();
                this.value = max.value;
                return max.remove();
            }
        }

        /**
         * Checks whether or not a value is present in the tree. If the value is
         * present, the node containing it will be splayed. If it isn't present
         * the last node visited by the search will be splayed (essentially the
         * last non-null node).
         *
         * @param x The value to check for.
         * @return a SplayResult with the result of the contains operation (true
         *         if the value was present, otherwise false) and the node that
         *         was splayed.
         */
        public SplayResult contains(E x) {
            Node node = this.find(x);
            node.splay();
            return new SplayResult(node.value.equals(x), node);
        }

        /**
         * Splays a node to the root of the tree.
         */
        private void splay() {
            if (this.parent == null) {
                // We have splayed this node to the root, we are done.
        		return;
        	}

            if (this.parent.parent == null) {
                // This node is one level away from the root.
            	if (this.isLeftChild()) {
            		this.zig();
    	        } else if (this.isRightChild()) {
    	        	this.zag();
    	        }
            } else {
           		if (this.isLeftChild() && this.parent.isLeftChild()) {
            		this.zigZig();
            	} else if (this.isRightChild() && this.parent.isRightChild()) {
    	        	this.zagZag();
    	        } else if (this.isRightChild() && this.parent.isLeftChild()) {
    	        	this.zigZag();
    	        } else if(this.isLeftChild() && this.parent.isRightChild()) {
    	        	this.zagZig();
    	        }
                this.splay();
           	}
        }

        /**
         * Performs a right rotation of the tree around this node.
         */
        private void rotateRight() {
            Node parent = this.parent;
            parent.replaceWith(this);
            parent.setLeftChild(this.rightChild);
            this.setRightChild(parent);
        }

        /**
         * Performs a left rotation of the tree around this node.
         */
        private void rotateLeft() {
            Node parent = this.parent;
            parent.replaceWith(this);
            parent.setRightChild(this.leftChild);
            this.setLeftChild(parent);
        }

        /**
         * Zig = right rotation.
         */
        private void zig() {
            this.rotateRight();
        }

        /**
         * Zag = left rotation.
         */
        private void zag() {
            this.rotateLeft();
        }

        /**
         * Zigs the parent of this node and then zigs this node.
         */
        private void zigZig() {
        	this.parent.zig();
        	this.zig();
        }

        /**
         * Zags the parent of this node and then zags this node.
         */
        private void zagZag() {
        	this.parent.zag();
        	this.zag();
        }

        /**
         * First zags and then zigs this node.
         */
        private void zigZag() {
        	this.zag();
        	this.zig();
        }

        /**
         * First zigs and then zags this node.
         */
        private void zagZig() {
        	this.zig();
        	this.zag();
        }

        public String toString() {
            StringBuilder builder = new StringBuilder("[ ");
            this.toStringRecursive(builder);
            builder.append("]");
            return builder.toString();
        }

        private void toStringRecursive(StringBuilder builder) {
            builder.append(this.value.toString());
            builder.append(" ");
            if (this.leftChild != null) {
                this.leftChild.toStringRecursive(builder);
            }
            if (this.rightChild != null) {
                this.rightChild.toStringRecursive(builder);
            }
        }
    }

    private Node root;
    private int size;

    /**
     * Initiates an empty Splay Tree.
     */
    public SplayTreeSet() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Returns the size of this tree.
     *
     * @return the size of this tree.
     */
    public int size() {
        return this.size;
    }

    /**
     * Adds a value to this tree.
     *
     * @param x the value to add.
     * @return true if the value was added (it wasn't present in the tree since
     *         before), otherwise false.
     */
    public boolean add(E x) {
        if (this.root == null) {
            this.root = new Node(x);
            this.size++;
            return true;
        } else {
            Node.SplayResult result = this.root.add(x);
            this.root = result.getNewRoot();
            if (result.getResult()) {
                this.size++;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Removes a value from this tree.
     *
     * @param x the value to remove.
     * @return true if the value was removed (it was present in the tree),
     *         otherwise false.
     */
    public boolean remove(E x) {
        if (this.root == null) {
            return false;
        } else {
            Node.SplayResult result = this.root.remove(x);
            this.root = result.getNewRoot();
            if (result.getResult()) {
                size--;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Checks whether or not a value is present in the tree.
     *
     * @param x The value to check for.
     * @return true if the value is present, otherwise false.
     */
    public boolean contains(E x) {
        if (this.root == null) {
            return false;
        } else {
            Node.SplayResult result = this.root.contains(x);
            this.root = result.getNewRoot();
            return result.getResult();
        }
    }

    public String toString() {
        if (this.root == null) {
            return "";
        } else {
            return this.root.toString();
        }
    }
}
