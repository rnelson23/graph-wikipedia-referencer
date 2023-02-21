package referencer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Document implements Serializable {
    public ArrayList<Edge> edges = new ArrayList<>();
    public HashMap<String, Double> words = new HashMap<>();
    public Document parent;
    public String link;
    public double best;
    public int pqIndex;

    public Document(String link) {
        this.link = link;
    }

    public String toString() {
        return link;
    }
}
