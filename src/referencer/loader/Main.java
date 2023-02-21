package referencer.loader;

import referencer.Graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Main {
    public static Graph graph = new Graph();

    public static void main(String[] args) throws IOException {
        File file = new File("links.txt");
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String link = scanner.nextLine();
            graph.addDocument(link);
        }

        graph.calculateTFIDF();
        graph.calculateWeights();

        System.out.println("There are " + graph.corpus.size() + " articles");

        FileOutputStream fileOutputStream = new FileOutputStream("data/graph");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(graph);
        objectOutputStream.flush();
        objectOutputStream.close();
    }
}
