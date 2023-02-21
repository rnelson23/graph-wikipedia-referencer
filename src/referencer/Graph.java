package referencer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Graph implements Serializable {
    public HashMap<String, Document> corpus = new HashMap<>();
    public HashMap<String, Double> glossary = new HashMap<>();

    public void addDocument(String link) throws IOException {
        Document document = corpus.get(link);

        if (document != null) {
            addEdges(document);
            return;
        }

        document = new Document(link);

        parseDocument(document);
        corpus.put(link, document);
        addEdges(document);
    }

    public void addEdges(Document src) throws IOException {
        org.jsoup.nodes.Document html = Jsoup.connect(src.link).get();
        Elements a = html.select("div.mw-parser-output > p > a:not(.new)");

        HashSet<String> links = new HashSet<>();

        for (Element element : a) {
            String link = element.attr("abs:href");

            if (link.contains("#")) continue;
            if (link.contains("\"")) continue;
            if (link.contains("File")) continue;
            if (link.contains("Special")) continue;

            links.add(link);
        }

        for (String link : links) {
            Document dst = corpus.get(link);

            if (dst == null) {
                dst = new Document(link);

                parseDocument(dst);
                corpus.put(link, dst);
            }

            Edge edge = new Edge(src, dst);
            if (dst.edges.contains(edge)) continue;

            src.edges.add(edge);
            dst.edges.add(edge);
        }
    }

    public void parseDocument(Document document) throws IOException {
        org.jsoup.nodes.Document html = Jsoup.connect(document.link).get();

        Elements h2 = html.select("div.mw-parser-output > h2");
        Elements h3 = html.select("div.mw-parser-output > h3");
        Elements p = html.select("div.mw-parser-output > p");

        String text = h2.text() + " " + h3.text() + " " + p.text();
        String[] words = text.toLowerCase().split("([^a-z'-][^a-z]*)");

        for (String word : words) {
            document.words.merge(word, 1D, Double::sum);
            glossary.put(word, null);
        }

        document.words.replaceAll((word, count) -> count / words.length);
    }

    public void calculateTFIDF() {
        glossary.replaceAll((word, idf) -> {
            int count = 0;

            for (Document document : corpus.values()) {
                if (document.words.containsKey(word)) count++;
            }

            return Math.log(corpus.size() / (double) count);
        });

        for (Document document : corpus.values()) {
            document.words.replaceAll((word, tf) -> tf * glossary.get(word));
        }
    }

    public void calculateWeights() {
        for (Document document : corpus.values()) {
            for (Edge edge : document.edges) {
                if (edge.weight != null) continue;

                double dotProduct = 0;
                double length1 = 0;
                double length2 = 0;

                HashMap<String, Double> srcWords = edge.src.words;
                HashMap<String, Double> dstWords = edge.dst.words;

                for (String word : srcWords.keySet()) {
                    double tfidf1 = srcWords.get(word);
                    double tfidf2 = dstWords.getOrDefault(word, 0D);

                    dotProduct += tfidf1 * tfidf2;

                    length1 += Math.pow(tfidf1, 2);
                    length2 += Math.pow(tfidf2, 2);
                }

                double lengths = Math.sqrt(length1) * Math.sqrt(length2);
                edge.weight = 1 - (dotProduct / lengths);
            }
        }
    }

    public Document[] calculatePath(Document src, Document dst) {
        Queue pq = new Queue(corpus, src);
        Document document;

        while ((document = pq.poll()) != null) {
            if (document == dst) break;

            for (Edge edge : document.edges) {
                Document s = edge.src;
                Document d = edge.dst;

                if (d.link.equals(document.link)) {
                    d = edge.src;
                    s = edge.dst;
                }

                double weight = s.best + edge.weight;

                if (weight < d.best) {
                    d.parent = s;
                    d.best = weight;
                    pq.resift(d);
                }
            }
        }

        ArrayList<Document> path = new ArrayList<>();

        for (Document d = dst; d != null; d = d.parent) {
            path.add(d);
            if (d.link.equals(src.link)) break;
        }

        Collections.reverse(path);
        if (!path.get(0).link.equals(src.link)) return null;
        return path.toArray(new Document[0]);
    }
}
