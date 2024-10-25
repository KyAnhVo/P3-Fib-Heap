public class BinomialHeapDemonstration {
  public static void main(String[] args) {
    System.out.println("Empty heap:");
    BinomialHeap<Integer, String> heap = new BinomialHeap<Integer, String>();
    BinomialHeap.Entry<Integer, String> entry;
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("One item:");
    heap.insert(100, "A");
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();
    
    System.out.println("Empty again:");
    entry = heap.deleteMin();
    System.out.println("deleteMin returned " + printEntry(entry));
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("One item again:");
    heap.insert(200, "A");
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Second item:");
    heap.insert(50, "B");
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Third item:");
    BinomialHeap.Entry<Integer, String> entryC = heap.insert(300, "C");
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Forth item:");
    BinomialHeap.Entry<Integer, String> entryD = heap.insert(250, "D");
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Delete:");
    entry = heap.deleteMin();
    System.out.println("deleteMin returned " + printEntry(entry));
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Forth item again:");
    BinomialHeap.Entry<Integer, String> entryE = heap.insert(400, "E");
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Decrease C's key:");
    heap.decreaseKey(entryC, 225);
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Decrease E's key:");
    heap.decreaseKey(entryE, 100);
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Invalid decreaseKey:");
    try {
      heap.decreaseKey(entryD, 250);
    } catch (Exception e) {
      System.out.println(e);
    }
    System.out.println();

    System.out.println("Delete entry:");
    heap.delete(entryC);
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Merge in empty heap:");
    heap.merge(new BinomialHeap<Integer, String>());
    entry = heap.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heap.printAll();
    System.out.println();

    System.out.println("Merge two heaps:");
    BinomialHeap<Integer, String> heapA = new BinomialHeap<Integer, String>();
    for (int i = 0; i <= 20; i += 2) {
      heapA.insert(i, "heapA:" + i);
    }
    BinomialHeap<Integer, String> heapB = new BinomialHeap<Integer, String>();
    for (int i = 1; i <= 31; i += 3) {
      heapB.insert(i, "heapB:" + i);
    }
    System.out.println("Heap A before:");
    heapA.printAll();
    entry = heapA.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    System.out.println("Heap B before:");
    heapB.printAll();
    entry = heapB.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    heapA.merge(heapB);
    System.out.println("Heap A after:");
    heapA.printAll();
    entry = heapA.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    System.out.println("Heap B after:");
    heapB.printAll();
    entry = heapB.findMin();
    System.out.println("findMin returned " + printEntry(entry));
    System.out.println();
  }

  private static <K extends Comparable<? super K>, V> String printEntry(
      BinomialHeap.Entry<K, V> entry) {
    if (entry == null) {
      return("null");
    } else {
      String tuple = "(" + entry.getKey() + ", " + entry.getValue() + ")";
      if (entry.inHeap()) {
        return tuple + " (in heap)";
      } else {
        return tuple + " (not in heap)";
      }
    }
  }
}
