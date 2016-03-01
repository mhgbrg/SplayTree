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
    }

    private int size;

    public SplayTreeSet() {
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public boolean add(E x) {
        size++;
        return false;
    }

    public boolean remove(E x) {
        size--;
        return false;
    }

    public boolean contains(E x) {
        return false;
    }
}
