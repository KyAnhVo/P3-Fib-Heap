/**
 * A 'simple' binomial heap implementation for CS 3345 at UTD.
 *
 * @param <K> The entries' key type.
 * @param <V> The entries' value type.
 */
public class BinomialHeap<K extends Comparable<? super K>, V> {

  /**
   * An element of the heap, consisting of a key-value pair.
   *
   * @param <K> The key type.
   * @param <V> The value type.
   */
  public static class Entry<K, V> {
    /**
     * Instantiate's a new entry.
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
     * Returns whether or not the entry still exists in the heap.
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

    private K key;
    private V value;
    private Node<K, V> node; // Node containing this Entry. Becomes null upon removal from heap.
  }

  /**
   * Instantiate an empty binomial heap.
   */
  BinomialHeap() {
    sentinelRoot = new Node<K, V>(null);
    sentinelRoot.prevSibling = sentinelRoot;
    sentinelRoot.nextSibling = sentinelRoot;
    minRoot = null;
    size = 0;
  }

  /**
   * Print all elements of the binomial heap.
   * Trees are printed in arbitrary order, each printed in a pre-order.
   * Individual elements are printed one line at a time, and each is indented a number of
   * '=' equal to twice its node's depth.
   * Printed elements should look like "(key, value)".
   * <p>
   * Runs in O(n) time for a heap of n elements.
   */
  public void printAll() {
    Node<K, V> currentRoot = sentinelRoot.nextSibling;
    while (currentRoot != sentinelRoot) {
      printAll(currentRoot, 0);
      currentRoot = currentRoot.nextSibling;
    }
  }

  /**
   * Inserts a new key-value pair into the binomial heap.
   * Allows for insertion of keys and values that are equal to ones already present.
   * <p>
   * Runs in worst-case O(log n) time.
   *
   * @param key Key for the value being inserted.
   * @param value The value being inserted.
   */
  public Entry<K, V> insert(K key, V value) {
    Entry<K, V> newEntry = new Entry<K, V>(key, value);
    Node<K, V> newNode = new Node<K, V>(newEntry);

    newNode.nextSibling = sentinelRoot.nextSibling;
    newNode.nextSibling.prevSibling = newNode;
    sentinelRoot.nextSibling = newNode;
    newNode.prevSibling = sentinelRoot;

    size++;

    combineTrees();

    return newEntry;
  }

  /**
   * Returns an Entry with the least key in the binomial heap or null if the heap is empty.
   * <p>
   * Runs in worst-case O(1) time.
   *
   * @returns An Entry storing the least key or null if the heap is empty.
   */
  public Entry<K, V> findMin() {
    if (size == 0) {
      return null;
    } else {
      return minRoot.entry;
    }
  }

  /**
   * Returns and removes Entry with the least key from the binomial heap.
   * <p>
   * Runs in worst-case O(log n) time.
   *
   * @return An Entry storing the least key.
   * @throws NoSuchElementException If the binomial heap is empty.
   */
  public Entry<K, V> deleteMin() {
    if (size == 0) {
      throw new java.util.NoSuchElementException();
    }

    Entry<K, V> minEntry = minRoot.entry;

    deleteRoot(minRoot);

    return minEntry;
  }

  /**
   * Decreases value of entry's key to newKey.
   * <p>
   * Runs in worst-case O(log n) time.
   *
   * @param entry Entry for which to update the key.
   * @param newKey New key for entry.
   * @throws IllegalArgumentException If entry is not a current member of the binomial heap or if
   * newKey is greater than entry's current key.
   */
  public void decreaseKey(Entry<K, V> entry, K newKey) {
    if (entry == null || entry.node == null || newKey.compareTo(entry.key) > 0) {
      throw new IllegalArgumentException();
    }

    entry.key = newKey;

    // Percolate up.
    Node<K, V> hole = entry.node;
    while (hole.parent != null && newKey.compareTo(hole.parent.entry.key) < 0) {
      hole.entry = hole.parent.entry;
      hole.entry.node = hole;
      hole = hole.parent;
    }
    hole.entry = entry;
    entry.node = hole;
  }

  /**
   * Removes entry from the binomial heap.
   * <p>
   * Runs in worst-case O(log n) time.
   *
   * @param entry Entry to remove from heap.
   * @throws IllegalArgumentException If entry is not a current member of the binomial heap.
   */
  public void delete(Entry<K, V> entry) {
    if (entry == null || entry.node == null) {
      throw new IllegalArgumentException();
    }

    // We don't know the minimum value for an arbitrary key type K, so we'll
    // have to manually percolate up and then remove.
    Node<K, V> hole = entry.node;
    while (hole.parent != null) {
      hole.entry = hole.parent.entry;
      hole.entry.node = hole;
      hole = hole.parent;
    }
    hole.entry = entry;
    entry.node = hole;

    deleteRoot(entry.node);
  }

  /**
   * Merges other binomial heap with current one, emptying the other heap in the process.
   * <p>
   * Runs in worst-case O(log n) time for two heaps of size at most n.
   *
   * @param other The other binomial heap from which to merge entries.
   * @throws IllegalArgumentException If other is null.
   */
  public void merge(BinomialHeap<K, V> other) {
    if (other == null) {
      throw new IllegalArgumentException();
    }

    if (other.size == 0) {
      return;
    }

    other.sentinelRoot.prevSibling.nextSibling = sentinelRoot.nextSibling;
    sentinelRoot.nextSibling.prevSibling = other.sentinelRoot.prevSibling;
    sentinelRoot.nextSibling = other.sentinelRoot.nextSibling;
    other.sentinelRoot.nextSibling.prevSibling = sentinelRoot;

    size += other.size;

    combineTrees();

    other.sentinelRoot.nextSibling = other.sentinelRoot;
    other.sentinelRoot.prevSibling = other.sentinelRoot;
    other.minRoot = null;
    other.size = 0;
  }

  /**
   * A binomial tree node.
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

    public int order; // Order of the binomial (sub)tree rooted at this node.

    /**
     * Instantiate a new node.
     *
     * @param entry The entry to store in the node.
     */
    Node(Entry<K, V> entry) {
      this.entry = entry;
      if (entry != null) {
        entry.node = this;
        sentinelChild = new Node<K, V>(null);
        sentinelChild.nextSibling = sentinelChild;
        sentinelChild.prevSibling = sentinelChild;
        sentinelChild.parent = this;
      }
    }
  }

  private Node<K, V> sentinelRoot;
  private Node<K, V> minRoot;
  private int size;

  /**
   * Print all elements of node's subtree in pre-order.
   * Members are printed one line at a time, and each is indented a number of spaces equal to its
   * node's depth.
   * <p>
   * Runs in O(n) time for a tree of n elements.
   *
   * @param node The root the subtree to be printed. If null, nothing is printed.
   * @param depth The depth of the node being printed. Assumed to be non-negative.
   */
  private void printAll(Node<K, V> node, int depth) {
    assert (depth >= 0);
    assert (node != null);
    assert (node.entry != null);

    StringBuilder builder = new StringBuilder();
    for (int spaces = 0; spaces < depth; spaces++) {
      builder.append("==");
    }
    builder.append("(");
    builder.append(node.entry.key);
    builder.append(", ");
    builder.append(node.entry.value);
    builder.append(")");
    System.out.println(builder.toString());
    
    Node<K, V> currentChild = node.sentinelChild.nextSibling;
    while (currentChild != node.sentinelChild) {
      printAll(currentChild, depth + 1);
      currentChild = currentChild.nextSibling;
    }
  }

  /**
   * Removes given root node from the heap, moving all of its children into the list of roots.
   * <p>
   * Runs in worst-case O(log n) time.
   *
   * @param root The root node to remove.
   */
  private void deleteRoot(Node<K, V> root) {
    assert(root != null && root.parent == null);

    Node<K, V> currentChild = root.sentinelChild.nextSibling;
    while (currentChild != root.sentinelChild) {
      Node<K, V> nextChild = currentChild.nextSibling;

      currentChild.nextSibling = sentinelRoot.nextSibling;
      sentinelRoot.nextSibling.prevSibling = currentChild;
      sentinelRoot.nextSibling = currentChild;
      currentChild.prevSibling = sentinelRoot;
      currentChild.parent = null;
      
      currentChild = nextChild;
    }
    root.prevSibling.nextSibling = root.nextSibling;
    root.nextSibling.prevSibling = root.prevSibling;

    root.entry.node = null;
    size--;

    combineTrees();
  }

  /**
   * Merges binomial trees until no two have the same order and updates minRoot.
   * <p>
   * Runs in worst-case O(log n) time.
   */
  private void combineTrees() {
    if (size == 0) { // No trees to combine.
      return;
    }

    // Assumes size <= 2^100.
    @SuppressWarnings("unchecked") // Unsafe cast, because Java does not create generic arrays.
    Node<K, V>[] rootsByOrder = (Node<K, V>[]) new Node[100];

    Node<K, V> currentRoot = sentinelRoot.nextSibling;
    Node<K, V> newMinRoot = currentRoot;
    while (currentRoot != sentinelRoot) {
      if (currentRoot.entry.key.compareTo(newMinRoot.entry.key) < 0) {
        newMinRoot = currentRoot;
      }

      int order = currentRoot.order;
      Node<K, V> otherRoot = rootsByOrder[order];
      if (otherRoot == null) { // currentRoot roots only known tree of its order.
        rootsByOrder[order] = currentRoot;
        currentRoot = currentRoot.nextSibling;
      } else { // Two trees of same order. Time to merge.
        // Remove otherRoot from list of roots.
        otherRoot.nextSibling.prevSibling = otherRoot.prevSibling;
        otherRoot.prevSibling.nextSibling = otherRoot.nextSibling;

        // If otherRoot has the lessor key, swap it with currentRoot.
        if (otherRoot.entry.key.compareTo(currentRoot.entry.key) < 0) {
          otherRoot.nextSibling = currentRoot.nextSibling;
          otherRoot.nextSibling.prevSibling = otherRoot;
          otherRoot.prevSibling = currentRoot.prevSibling;
          otherRoot.prevSibling.nextSibling = otherRoot;

          Node<K, V> temp = currentRoot;
          currentRoot = otherRoot;
          otherRoot = temp;
        }

        // Add greater keyed root as child of lessor keyed root.
        otherRoot.nextSibling = currentRoot.sentinelChild.nextSibling;
        otherRoot.nextSibling.prevSibling = otherRoot;
        otherRoot.prevSibling = currentRoot.sentinelChild;
        currentRoot.sentinelChild.nextSibling = otherRoot;
        otherRoot.parent = currentRoot;
        currentRoot.order++;

        rootsByOrder[order] = null;
        // Intentially do not update currentRoot incase it now has same order as another tree.
      }
    }

    minRoot = newMinRoot;
  }
}
