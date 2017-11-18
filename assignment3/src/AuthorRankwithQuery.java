import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yansong on 11/16/17.
 */
public class AuthorRankwithQuery {
    String q1 = "Data Mining";
    String q2 = "Information Retrieval";
    final static String indexPath = "/Users/yansong/Programming" +
            "/search/SONG-information-retrevial/assignment3/author_index";

    public static void getPrior(String queryString, Map<String, Double> priorMap)
            throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("TEXT", analyzer);
        Query query = parser.parse(queryString);
        TopDocs topDocs = searcher.search(query, 300);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
    }

    public static void main(String[] args) throws IOException {
        DirectedSparseGraph graph = new DirectedSparseGraph();
        HashMap<String, String> vMap = new HashMap<>();
        AuthorRank.getGraph(AuthorRank.getFilePath(), graph, vMap);
    }
}
