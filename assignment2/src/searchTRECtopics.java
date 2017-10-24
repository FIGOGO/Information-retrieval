import com.sun.tools.hat.internal.server.QueryListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * Created by yansong on 10/5/17.
 */
public class searchTRECtopics {
    final static String topicFilePath = "/Users/yansong/Programming/search" +
                "/SONG-information-retrevial/assignment2/topics/topics.51-100";
    final static String indexDirPath = "/Users/yansong/Programming/search" +
            "/SONG-information-retrevial/assignment2/index";

    public static void printResult(ArrayList<PriorityQueue<ScoreDocument>> pqList, String queryType) throws IOException {
        String method = "TREC";
        String fileName = method + "_" + queryType + ".txt";
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        int topicId = 51;
        for (PriorityQueue<ScoreDocument> pq : pqList) {
            for (int rank = 1; rank <= 1000; rank++) {
                ScoreDocument sd = pq.poll();
                if (sd.getScore() == 0) break;
                printWriter.printf("%2d %d %s %4d %2.5f %s \n",
                        topicId, 0, sd.getDoc().get("DOCNO"), rank, sd.getScore(), method+"_"+queryType);
            }
            topicId++;
        }
        printWriter.close();
    }

    public static void searchTopic(ArrayList<Query> queryList, IndexReader reader, String queryType) throws IOException {
        IndexSearcher searcher = new IndexSearcher(reader);
        ArrayList<PriorityQueue<ScoreDocument>> pqList = new ArrayList<>();
        ArrayList<ScoreDocument> docArray = easySearch.extractDocs(reader);
        for (Query q : queryList) {
            Set<Term> queryTerms = new LinkedHashSet<>();
            searcher.createNormalizedWeight(q, false).extractTerms(queryTerms);
            ArrayList<ScoreDocument> sdArray = easySearch.searchScore(docArray, queryTerms, reader);
            PriorityQueue<ScoreDocument> pq = new PriorityQueue<>();
            pq.addAll(sdArray);
            pqList.add(pq);
        }
        searchTRECtopics.printResult(pqList, queryType);
    }

    public static void main(String[] args) throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirPath));
        IndexReader reader = DirectoryReader.open(indexDirectory);

        Analyzer analyzer = new StandardAnalyzer();
        ExtractQueryFromTopics extractor = new ExtractQueryFromTopics();
        ArrayList<Query> queryListShort = extractor.extractQuery
                (topicFilePath, "<title>", "\n", analyzer);
        searchTRECtopics.searchTopic(queryListShort, reader, "short");
        ArrayList<Query> queryListLong = ExtractQueryFromTopics.extractQuery
                (topicFilePath, "<desc>", "<smry>", new StandardAnalyzer());
        searchTRECtopics.searchTopic(queryListLong, reader, "long");

    }
}
