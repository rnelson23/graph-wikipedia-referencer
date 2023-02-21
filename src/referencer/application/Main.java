package referencer.application;

import referencer.Document;
import referencer.Graph;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static Graph graph;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("data/graph");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        graph = (Graph) objectInputStream.readObject();
        objectInputStream.close();

        File file = new File("links.txt");
        Scanner scanner = new Scanner(file);

        ArrayList<Document> documents = new ArrayList<>();

        while (scanner.hasNextLine()) {
            documents.add(graph.corpus.get(scanner.nextLine()));
        }

        int numSets = 0;

        while (documents.size() > 0) {
            Document src = documents.get(0);
            numSets++;

            documents.remove(0);
            documents.removeIf(dst -> graph.calculatePath(src, dst) != null);
        }

        System.out.println("There are " + graph.corpus.size() + " articles and " + numSets + " disjoint sets");

        GUI gui = new GUI();
        gui.initializeGUI();
    }
}
