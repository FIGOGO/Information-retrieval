import org.apache.lucene.document.Document;

import java.util.PriorityQueue;

/**
 * Created by yansong on 10/22/17.
 */
public class ScoreDocument implements Comparable<ScoreDocument>{
    double score;
    Document doc;
    double docLength;

    public ScoreDocument(double s, Document d, double l) {
        this.score = s;
        this.doc = d;
        this.docLength = l;
    }
    public ScoreDocument(Document d, double l) {
        this(0.0, d, l);
    }
    public ScoreDocument(double s, Document d) {
        this(s, d, 0.0);
    }

    double getScore() {
        return score;
    }
    int setScore(double s) {
        this.score = s;
        return 0;
    }
    int addScore(double s) {
        this.score += s;
        return 0;
    }
    Document getDoc() {
        return this.doc;
    }
    double getDocLength(){
        return this.docLength;
    }

    @Override
    public int compareTo(ScoreDocument sd) {
        return Double.compare(sd.score, this.score);
    }

    @Override
    public ScoreDocument clone() {
        return new ScoreDocument(this.doc, this.docLength);
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
