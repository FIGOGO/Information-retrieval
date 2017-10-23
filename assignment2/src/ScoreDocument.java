import org.apache.lucene.document.Document;

import java.util.PriorityQueue;

/**
 * Created by yansong on 10/22/17.
 */
public class ScoreDocument implements Comparable<ScoreDocument>{
    double score;
    Document doc;

    ScoreDocument(double s, Document d) {
        score = s;
        doc = d;
    }

    double getScore() {
        return score;
    }
    int setScore(double s) {
        this.score = s;
        return 0;
    }
    Document getDoc() {
        return doc;
    }

    @Override
    public int compareTo(ScoreDocument sd) {
        return Double.compare(sd.score, this.score);
    }

    public static void main(String[] args) {
        PriorityQueue<ScoreDocument> pq = new PriorityQueue<>();
        pq.add(new ScoreDocument(1, new Document()));
        pq.add(new ScoreDocument(3, new Document()));
        pq.add(new ScoreDocument(0, new Document()));
        pq.add(new ScoreDocument(2, new Document()));
        ScoreDocument sd = pq.poll();
        System.out.println(sd.score);
    }

}
