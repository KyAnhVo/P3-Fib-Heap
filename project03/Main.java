public class Main {
    public static void main(String[] args) {
        // Create a new Fibonacci heap
        FibonacciHeap<Integer, String> heap = new FibonacciHeap<>();

        // Test 1: Insertions
        FibonacciHeap.Entry<Integer, String> entry1 = heap.insert(5, "Value 5");
        FibonacciHeap.Entry<Integer, String> entry2 = heap.insert(3, "Value 3");
        FibonacciHeap.Entry<Integer, String> entry3 = heap.insert(7, "Value 7");
        FibonacciHeap.Entry<Integer, String> entry4 = heap.insert(2, "Value 2");
        FibonacciHeap.Entry<Integer, String> entry5 = heap.insert(8, "Value 8");

        System.out.println("Heap after insertions:");
        heap.printAll();

        // Test 2: Find minimum
        System.out.println("\nMinimum entry: " + heap.findMin().getKey() + " -> " + heap.findMin().getValue());

        // Test 3: Delete minimum
        System.out.println("\nDeleting minimum...");
        heap.deleteMin();
        System.out.println("Heap after deleting minimum:");
        heap.printAll();

        // Test 4: Decrease key
        System.out.println("\nDecreasing key of entry (7, Value 7) to 1...");
        heap.decreaseKey(entry3, 1);
        System.out.println("Heap after decreasing key:");
        heap.printAll();
        System.out.println("New minimum entry: " + heap.findMin().getKey() + " -> " + heap.findMin().getValue());

        // Test 5: Delete entry
        System.out.println("\nDeleting entry (8, Value 8)...");
        heap.delete(entry5);
        System.out.println("Heap after deleting entry (8, Value 8):");
        heap.printAll();

        // Test 6: Merge two heaps
        FibonacciHeap<Integer, String> otherHeap = new FibonacciHeap<>();
        otherHeap.insert(6, "Value 6");
        otherHeap.insert(0, "Value 0");

        System.out.println("\nOther heap before merging:");
        otherHeap.printAll();

        System.out.println("\nMerging heaps...");
        heap.merge(otherHeap);

        System.out.println("Heap after merging:");
        heap.printAll();

        // Verify that the other heap is now empty
        System.out.println("\nOther heap after merging (should be empty):");
        otherHeap.printAll();
    }
}