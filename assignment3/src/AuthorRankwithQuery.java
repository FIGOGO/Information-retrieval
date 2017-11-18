import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by yansong on 11/16/17.
 */
public class AuthorRankwithQuery {
    final static String q1 = "Data Mining";
    final static String q2 = "Information Retrieval";
    final static String indexPath = "/Users/yansong/Programming" +
            "/search/SONG-information-retrevial/assignment3/author_index";

    public static void getPrior(String queryString, Map<String, Double> priorMap,
                                Map<String, String> vMapReserved)
            throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        searcher.setSimilarity(new BM25Similarity());
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse(queryString);
        TopDocs topDocs = searcher.search(query, 300);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        // Initialize the priorMap
        for (String value : vMapReserved.values()) {
            priorMap.put(value, 0.0);
        }
        double scoreSum = 0;
        for (ScoreDoc sd : scoreDocs) {
            Document d = searcher.doc(sd.doc);
            String authorid = d.get("authorid");
            String nodeid = vMapReserved.get(authorid);
            scoreSum += sd.score;
            priorMap.put(nodeid, priorMap.get(nodeid) + sd.score);
        }
        // Normalize
        for (String key : priorMap.keySet()) {
             Double value = priorMap.get(key);
             priorMap.put(key, value/scoreSum);
        }
    }

    public static void printResults(PageRankWithPriors ranker, Map<String, String> vMap) {
        // TreeMap with key decreasing order
        Map<Double, String> tMap = new TreeMap<>(Collections.reverseOrder());
        for (String v : vMap.keySet()) {
            double score = (double) ranker.getVertexScore(v);
            tMap.put(score, vMap.get(v));
        }
        Set s = tMap.entrySet();
        Iterator iterator = s.iterator();
        for (int i = 0; i < 10; i++) {
            Map.Entry me = (Map.Entry) iterator.next();
            System.out.println("Author " + me.getValue() + " has score " + me.getKey());
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        DirectedSparseGraph<String, String> graph = new DirectedSparseGraph<>();
        // vMap maps Integers to Author ID
        HashMap<String, String> vMap = new HashMap<>();
        AuthorRank.getGraph(AuthorRank.getFilePath(), graph, vMap);

        // Reverse the vMap: Author ID -> Integers
        HashMap<String, String> vMapReversed = new HashMap<>();
        for (String key : vMap.keySet()) {
            String value = vMap.get(key);
            vMapReversed.put(value, key);
        }

        double alpha = 0.15;
        HashMap<String, Double> priorMap1 = new HashMap<>();
        getPrior(q1, priorMap1, vMapReversed);
        Transformer<String, Double> transformer1 = MapTransformer.getInstance(priorMap1);
        PageRankWithPriors<String, String> ranker1
                = new PageRankWithPriors(graph, transformer1, alpha);
        ranker1.evaluate();
        System.out.println("");
        System.out.println("Result for query: " + q1);
        printResults(ranker1, vMap);
        System.out.println("");

        HashMap<String, Double> priorMap2 = new HashMap<>();
        getPrior(q2, priorMap2, vMapReversed);
        Transformer<String, Double> transformer2 = MapTransformer.getInstance(priorMap2);
        PageRankWithPriors<String, String> ranker2
                = new PageRankWithPriors(graph, transformer2, alpha);
        ranker2.evaluate();
        System.out.println("Result for query: " + q2);
        printResults(ranker2, vMap);
    }
}
