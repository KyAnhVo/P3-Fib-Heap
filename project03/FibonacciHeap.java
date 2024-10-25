import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation file for CS 3345.HON.24F Programming Assignment #3.
 *
 * @param <K> The entries' key type.
 * @param <V> The entries' value type.
 */
public final class FibonacciHeap <K extends Comparable<? super K>, V> {

    //
    // PROPERTIES
    //

    private Node<K, V> sentinelRoot;
    private Node<K, V> minRoot;
    private int size;

    //
    // METHODS
    //

    /**
     * Instantiate an empty Fibonacci heap.
     */
    FibonacciHeap() {
        size = 0;
        sentinelRoot = new Node<>(null);
        sentinelRoot.parent = null;
        sentinelRoot.nextSibling = sentinelRoot;
        sentinelRoot.prevSibling = sentinelRoot;
        sentinelRoot.sentinelChild = sentinelRoot;
        minRoot = sentinelRoot;
    }

    /**
     * Print all elements of the Fibonacci heap.
     * Trees are printed in arbitrary order, each printed in a pre-order.
     * Individual elements are printed one line at a time, and each is indented a number of
     * '=' equal to twice its node's depth.
     * Printed elements should look like "(key, value) (marked)" or "(key, value)
     * (not marked)" depending on whether they are marked.
     * <p>
     * Runs in O(n) time for a heap of n elements.
     */
    public void printAll() {
        Node<K, V> curr = sentinelRoot.nextSibling;
        while (curr != sentinelRoot) {
            printAll(curr, 0);
            curr = curr.nextSibling;
        }
    }

    public void printAll(Node<K, V> node, int depth)
    {
        assert (depth >= 0);
        assert (node != null);
        assert (node.entry != null);

        StringBuilder builder = new StringBuilder();
        for (int spaces = 0; spaces < depth; spaces++) {
            builder.append("==");
        }

        Entry<K, V> entry = node.entry;
        System.out.print("==".repeat(depth));
        entry.printEntry();

        Node<K, V> currentChild = node.sentinelChild.nextSibling;
        while (currentChild != node.sentinelChild)
        {
            printAll(currentChild, depth + 1);
            currentChild = currentChild.nextSibling;
        }
    }



    /**
     * Inserts a new key-value pair into the Fibonacci heap.
     * Allows for insertion of keys and values that are equal to ones already present.
     * <p>
     * Runs in worst-case O(1) time and amortized O(1) time.
     *
     * @param key Key for the value being inserted.
     * @param value The value being inserted.
     */
    public Entry<K, V> insert(K key, V value) {
        Node<K, V> newNode = new Node<>(new Entry<K, V>(key, value));
        size++;

        newNode.prevSibling = sentinelRoot;
        newNode.nextSibling = sentinelRoot.nextSibling;
        sentinelRoot.nextSibling.prevSibling = newNode;
        sentinelRoot.nextSibling = newNode;

        if (minRoot == sentinelRoot) minRoot = newNode;
        else
        {
            int cmp = key.compareTo(minRoot.entry.getKey());
            if (cmp < 0) minRoot = newNode;
        }
        return newNode.entry;
    }

    /**
     * Returns an Entry with the least key in the Fibonacci heap or null if the heap is empty.
     * <p>
     * Runs in worst-case O(1) time and amortized O(1) time.
     *
     * @returns An Entry storing the least key or null if the heap is empty.
     */
    public Entry<K, V> findMin() {
        return minRoot.entry;
    }

    /**
     * Returns and removes Entry with the least key from the Fibonacci heap.
     * <p>
     * Runs in worst-case O(n) time and amortized O(log n) time.
     *
     * @return An Entry storing the least key.
     * @throws NoSuchElementException If the Fibonacci heap is empty.
     */
    public Entry<K, V> deleteMin() {
        if (size == 0) throw new NoSuchElementException("Heap is empty");

        size--;

        if (size == 1)
        {
            Node<K, V> curr = sentinelRoot.nextSibling;
            Entry<K, V> entry = curr.entry;
            entry.node = null;
            sentinelRoot.nextSibling = sentinelRoot;
            sentinelRoot.prevSibling = sentinelRoot;
            minRoot = sentinelRoot;
            return entry;
        }

        Entry<K, V> minEntry = minRoot.entry;
        Node<K, V> node = minRoot,
                prevSibling = node.prevSibling,
                nextSibling = node.nextSibling,
                firstChild = node.sentinelChild.nextSibling,
                lastChild = node.sentinelChild.prevSibling;

        // delete node, put node's children onto root

        // Case 1: node has child(ren)
        if (firstChild != node.sentinelChild)
        {
            prevSibling.nextSibling = firstChild;
            firstChild.prevSibling = prevSibling;
            nextSibling.prevSibling = lastChild;
            lastChild.nextSibling = nextSibling;
            while (firstChild != lastChild) {
                firstChild.parent = null;
                firstChild = firstChild.nextSibling;
            }
            lastChild.parent = null;
        }
        // Case 2: node has no children
        else
        {
            prevSibling.nextSibling = nextSibling;
            nextSibling.prevSibling = prevSibling;
        }
        minEntry.node = null;

        // Get all nodes into rank-based array to combineTree
        @SuppressWarnings("unchecked")
        Stack<Node<K, V>>[] rankArr = (Stack<Node<K,V>>[]) new Stack[100]; // Assume no node rank >100.
        Node<K, V> currRoot = sentinelRoot.nextSibling;

        while (currRoot != sentinelRoot)
        {
            // Insert currRoot into stack at arr[currRoot.rank]
            int currRank = currRoot.rank;
            if (rankArr[currRank] == null) rankArr[currRank] = new Stack<Node<K, V>>();
            rankArr[currRank].push(currRoot);
            // increment currRoot by 1
            currRoot = currRoot.nextSibling;
        }

        // merge trees with same rank until only 1 left

        for (Stack<Node<K, V>> roots : rankArr)
        {
            if (roots == null) continue;
            while (roots.size() >= 2) {
                Node<K, V> curr = merge(roots.pop(), roots.pop());
                if (rankArr[curr.rank] == null) rankArr[curr.rank] = new Stack<Node<K, V>>();
                rankArr[curr.rank].push(curr);
            }
        }

        // Set new minRoot
        resetMinRoot();

        return minEntry;
    }

    private void resetMinRoot()
    {
        // Set new minRoot
        minRoot = sentinelRoot.nextSibling;
        Node<K, V> currRoot = sentinelRoot.nextSibling;
        while (currRoot != sentinelRoot)
        {
            if (currRoot.entry.getKey().compareTo(minRoot.entry.getKey()) < 0)
                minRoot = currRoot;
            currRoot = currRoot.nextSibling;
        }
    }

    /**
     * Merge the 2 roots, and return the smaller root.
     *
     * @param first
     * @param second
     * @return the smaller node (the root of
     */
    private Node<K, V> merge(Node<K, V> first, Node<K, V> second)
    {
        // MUST BE ROOTS
        assert (first != null && first.parent == null);
        assert (second != null && second.parent == null);

        // determining the larger/smaller of the bunch
        Node<K, V> small = (first.entry.key.compareTo(second.entry.key) < 0) ? first : second,
                large = (first == small) ? second : first;

        // attach large under small

        // prep variables
        Node<K, V> largePrevSibling = large.prevSibling,
                largeNextSibling = large.nextSibling,
                smallFirstChild = small.sentinelChild.nextSibling;
        // detach large from root
        largePrevSibling.nextSibling = largeNextSibling;
        largeNextSibling.prevSibling = largePrevSibling;
        // attach large as small's new first child
        small.sentinelChild.nextSibling = large;
        smallFirstChild.prevSibling = large;
        large.prevSibling = small.sentinelChild;
        large.nextSibling = smallFirstChild;
        large.parent = small;
        // increase small's rank by large's rank + 1 (because large)
        small.rank += large.rank + 1;

        return small;
    }

    /**
     * Decreases value of entry's key to newKey.
     * <p>
     * Runs in worst-case O(log n) time and amortized O(1) time.
     *
     * @param entry Entry for which to update the key.
     * @param newKey New key for entry.
     * @throws IllegalArgumentException If entry is not a current member of the Fibonacci heap or if
     * newKey is greater than entry's current key.
     */
    public void decreaseKey(Entry<K, V> entry, K newKey) {
        if (entry.node == null) // entry not in heap
            throw new IllegalArgumentException("Entry not found");
        if (entry.getKey().compareTo(newKey) < 0) // newKey > key
            throw new IllegalArgumentException("New entry keys must be lower than or equal to key");
        if (entry.getKey().equals(newKey)) // No job to be done
            return;

        entry.key = newKey;
        Node<K, V> node = entry.node;

        // Case: root
        if (node.parent == null) return;

        // Case: does not violate heap property
        if (newKey.compareTo(node.parent.entry.key) >= 0)
            return;

        // Case: Violates heap property
        decreaseKeyRecursiveStep(node);

        // Reset minRoot
        resetMinRoot();
    }

    public void decreaseKeyRecursiveStep(Node<K, V> node)
    {
        assert (node.parent != null);

        Node<K, V> prevSibling = node.prevSibling,
                nextSibling = node.nextSibling,
                parent = node.parent,
                ancestor = parent;

        // detach node from parent
        prevSibling.nextSibling = nextSibling;
        nextSibling.prevSibling = prevSibling;

        // attach node as new root
        node.prevSibling = sentinelRoot;
        node.nextSibling = sentinelRoot.nextSibling;
        sentinelRoot.nextSibling.prevSibling = node;
        sentinelRoot.nextSibling = node;
        node.parent = null;

        // decrease rank of all ancestors
        while (ancestor.parent != null)
        {
            ancestor.rank -= (node.rank + 1); // because rank does not consider Node node.
            ancestor = ancestor.parent;
        }
        ancestor.rank -= node.rank; // root was not handled in while loop, handled here

        // consider parent
        if (parent.markedForDeletion) decreaseKeyRecursiveStep(parent);
        else parent.markedForDeletion = true;
    }

    /**
     * Removes entry from the Fibonacci heap.
     * <p>
     * Runs in worst-case O(n) time and amortized O(log n) time.
     *
     * @param entry Entry to remove from heap.
     * @throws IllegalArgumentException If entry is not a current member of the Fibonacci heap.
     */
    public void delete(Entry<K, V> entry) {
        Node<K, V> node = entry.node;
        if (node == null) throw new IllegalArgumentException("entry is null");

        if (node.parent != null)
            decreaseKeyRecursiveStep(node);
        minRoot = node;
        deleteMin();
    }

    /**
     * Merges other Fibonacci heap with current one, emptying the other heap in the process.
     * <p>
     * Runs in worst-case O(1) time and amortized O(1) time.
     *
     * @param other The other Fibonacci heap from which to merge entries.
     * @throws IllegalArgumentException If other is null.
     */
    public void merge(FibonacciHeap<K, V> other) {
        if (other == null) throw new IllegalArgumentException("other is null");

        Node<K, V> otherFirst = other.sentinelRoot.nextSibling,
                otherLast = other.sentinelRoot.prevSibling;

        otherLast.nextSibling = this.sentinelRoot;
        otherFirst.prevSibling = this.sentinelRoot.prevSibling;
        this.sentinelRoot.prevSibling.nextSibling = otherFirst;
        this.sentinelRoot.prevSibling = otherLast;

        size += other.size;
        this.minRoot = (this.minRoot.entry.key.compareTo(other.minRoot.entry.key) < 0) ? minRoot : other.minRoot;

        // Empty other's heap
        other.sentinelRoot.nextSibling = other.sentinelRoot;
        other.sentinelRoot.prevSibling = other.sentinelRoot;
        other.size = 0;
        other.minRoot = other.sentinelRoot;
    }

    //
    // ENTRIES AND NODES
    //

    /**
     * Entry: containing key, value, and Node.
     * @param <K>
     * @param <V>
     */
    public static class Entry<K, V> {
        /**
         * Instantiates a new entry.
         * Marked private so only the heap itself can create new entry instances.
         *
         * @param key The entry's key.
         * @param value The entry's value.
         */
        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns whether the entry still exists in the heap.
         *
         * @return true if entry has not been deleted from the heap.
         */
        public boolean inHeap() {
            return node != null;
        }

        /**
         * Returns key for this entry.
         *
         * @return Key for this entry.
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns value of this entry.
         *
         * @return Value of this entry.
         */
        public V getValue() {
            return value;
        }

        public void printEntry()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            builder.append(getKey());
            builder.append(", ");
            builder.append(getValue());
            builder.append(") ");
            System.out.print(builder.toString());
            System.out.printf("(%s)\n", node.markedForDeletion ? "marked" : "not marked");
        }

        private K key;
        private V value;
        private Node<K, V> node; // Node containing this Entry. Becomes null upon removal from heap.
    }

    /**
     * A Fibonacci tree node.
     *
     * @param <K> The entry's key type.
     * @param <V> The entry's value type.
     */
    private static class Node<K, V> {
        public Entry<K, V> entry;

        // Each set of sibling nodes is stored in a circular doubly linked list with a sentinel node
        // distinguished by a null entry.
        // The "last" entry's next link and "first" entry's previous link go to the sentinel.
        // Roots of the binomial trees are linked as if they are siblings.
        //
        // There are certainly more efficient ways to store each tree, but these extra links will make
        // it easier to implement a Fibonacci heap.
        public Node<K, V> nextSibling;
        public Node<K, V> prevSibling;
        public Node<K, V> sentinelChild;
        public Node<K, V> parent;
        public boolean markedForDeletion;
        public int rank;


        public int order; // Order of the binomial (sub)tree rooted at this node.

        /**
         * Instantiate a new node.
         *
         * @param entry The entry to store in the node.
         */
        Node(Entry<K, V> entry) {
            rank = 0;   // No child
            this.entry = entry;
            this.markedForDeletion = false;
            if (entry != null) {
                entry.node = this;
                sentinelChild = new Node<K, V>(null);
                sentinelChild.nextSibling = sentinelChild;
                sentinelChild.prevSibling = sentinelChild;
                sentinelChild.parent = this;
            }
        }
    }
}
