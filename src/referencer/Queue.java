package referencer;

import java.io.Serializable;
import java.util.HashMap;

public class Queue implements Serializable {
    public final Document[] array;
    public int size = 1;

    public static int leftOf(int k) { return (k << 1) + 1; }
    public static int rightOf(int k) { return leftOf(k) + 1; }
    public static int parentOf(int k) { return (k - 1) >>> 1; }

    public Queue(HashMap<String, Document> corpus, Document root) {
        root.best = 0;
        root.pqIndex = 0;

        array = new Document[corpus.size()];
        array[0] = root;

        for (Document document : corpus.values()) {
            document.parent = null;

            if (!document.link.equals(root.link)) {
                document.best = Double.MAX_VALUE;
                document.pqIndex = size;

                array[size] = document;
                size++;
            }
        }
    }

    public void resift(Document document) {
        int index = document.pqIndex;

        while (index > 0) {
            int parentIndex = parentOf(index);
            Document parent = array[parentIndex];

            if (document.best >= parent.best) break;

            parent.pqIndex = index;
            array[index] = parent;

            index = parentIndex;
        }

        document.pqIndex = index;
        array[index] = document;
    }

    public Document poll() {
        if (size == 0) return null;

        Document least = array[0];
        if (least.best == Double.MAX_VALUE) return null;

        size--;

        if (size > 0) {
            Document document = array[size];
            array[size] = null;

            int leftIndex;
            int index = 0;

            while ((leftIndex = leftOf(index)) < size) {
                Document leftChild = array[leftIndex];
                int rightIndex = leftIndex + 1;

                if (rightIndex < size) {
                    Document rightChild = array[rightIndex];

                    if (leftChild.best > rightChild.best) {
                        leftChild = rightChild;
                        leftIndex = rightIndex;
                    }
                }

                if (document.best <= leftChild.best) break;

                leftChild.pqIndex = index;
                array[index] = leftChild;

                index = leftIndex;
            }

            document.pqIndex = index;
            array[index] = document;
        }

        return least;
    }
}
