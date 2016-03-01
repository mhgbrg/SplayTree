import java.util.*;

public class SplayTreeSet<E extends Comparable<? super E>> implements SimpleSet<E> {
    private class SplayResult {
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

    private class Node {
        private E value;
        private Node parent;
        private Node leftChild;
        private Node rightChild;

        public Node(E value) {
            this.value = value;
        }

        private boolean isLeftChild() {
            return this.parent != null && this.parent.leftChild == this;
        }

        private boolean isRightChild() {
            return this.parent != null && this.parent.rightChild == this;
        }

        private void setLeftChild(Node node) {
            this.leftChild = node;
            if (node != null) {
                node.parent = this;
            }
        }

        private void setRightChild(Node node) {
            this.rightChild = node;
            if (node != null) {
                node.parent = this;
            }
        }

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

        private Node findMax() {
            if (this.rightChild == null) {
                return this;
            } else {
                return this.rightChild.findMax();
            }
        }

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

        public SplayResult contains(E x) {
            Node node = this.find(x);
            node.splay();
            return new SplayResult(node.value.equals(x), node);
        }

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

        private void rotateRight() {
            Node parent = this.parent;
            parent.replaceWith(this);
            parent.setLeftChild(this.rightChild);
            this.setRightChild(parent);
        }

        private void rotateLeft() {
            Node parent = this.parent;
            parent.replaceWith(this);
            parent.setRightChild(this.leftChild);
            this.setLeftChild(parent);
        }

        private void zig() {
            this.rotateRight();
        }

        private void zag() {
            this.rotateLeft();
        }

        private void zigZig() {
        	this.parent.zig();
        	this.zig();
        }

        private void zagZag() {
        	this.parent.zag();
        	this.zag();
        }

        private void zigZag() {
        	this.zag();
        	this.zig();
        }

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

    public SplayTreeSet() {
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public boolean add(E x) {
        if (this.root == null) {
            this.root = new Node(x);
            this.size++;
            return true;
        } else {
            SplayResult result = this.root.add(x);
            this.root = result.getNewRoot();
            if (result.getResult()) {
                this.size++;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean remove(E x) {
        if (this.root == null) {
            return false;
        } else {
            SplayResult result = this.root.remove(x);
            this.root = result.getNewRoot();
            if (result.getResult()) {
                size--;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean contains(E x) {
        if (this.root == null) {
            return false;
        } else {
            SplayResult result = this.root.contains(x);
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
