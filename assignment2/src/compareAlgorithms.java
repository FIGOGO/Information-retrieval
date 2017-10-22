import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yansong on 10/5/17.
 */
public class compareAlgorithms {
    final static String topicFilePath = "/Users/yansong/Programming/search" +
                "/SONG-information-retrevial/assignment2/topics/topics.51-100";
    final static String indexDirPath = "/Users/yansong/Programming/search" +
            "/SONG-information-retrevial/assignment2/index";
    static HashMap<String, Similarity> hashMap = new HashMap<>();

    compareAlgorithms(){
        hashMap.put("BM25", new BM25Similarity());
        hashMap.put("VSM", new ClassicSimilarity());
        hashMap.put("LMwDS",  new LMDirichletSimilarity());
        hashMap.put("LMwJMS", new LMJelinekMercerSimilarity((float) 0.7));
    }

    public void callMethod(String method, IndexSearcher searcher, List<Query> queryList, String queryType) throws IOException {
        searcher.setSimilarity(hashMap.get(method));
        String fileName = method + "_" + queryType + ".txt";
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        int topicId = 51;
        for (Query q : queryList) {
            TopDocs topDocs = searcher.search(q, 1000);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            int rank = 1;
            for (ScoreDoc sd : scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                printWriter.printf("%2d %d %s %4d %2.5f %s \n",
                        topicId, 0, doc.get("DOCNO"), rank++, sd.score, method+"_"+queryType);
            }
            topicId++;
        }
        printWriter.close();
    }

    public static void main(String[] args) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirPath)));
        IndexSearcher searcher = new IndexSearcher(reader);

        ArrayList<Query> queryListShort = ExtractQueryFromTopics.extractQuery
                (topicFilePath, "<title>", "\n", new StandardAnalyzer());
        ArrayList<Query> queryListLong = ExtractQueryFromTopics.extractQuery
                (topicFilePath, "<desc>", "<smry>", new StandardAnalyzer());
        compareAlgorithms compare = new compareAlgorithms();
        for (String s: hashMap.keySet()){
            compare.callMethod(s, searcher, queryListShort, "short");
            compare.callMethod(s, searcher, queryListLong, "long");
        }


    }
}
