package referencer;

import java.io.Serializable;

public class Edge implements Serializable {
    public Document src;
    public Document dst;
    public Double weight;

    public Edge(Document src, Document dst) {
        this.src = src;
        this.dst = dst;
    }
}
