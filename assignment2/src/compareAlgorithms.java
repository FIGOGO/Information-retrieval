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
import java.nio.file.Paths;

/**
 * Created by yansong on 10/5/17.
 */
public class compareAlgorithms {
    final static String topicFilePath = "/Users/yansong/Programming/search" +
                "/SONG-information-retrevial/assignment2/topics/topics.51-100";
    final static String indexDirPath = "/Users/yansong/Programming/search" +
            "/SONG-information-retrevial/assignment2/index";

    public ScoreDoc[] bm25() {}
    public ScoreDoc[] vsm() {}
    public ScoreDoc[] lmwds() {}
    public ScoreDoc[] lmwjms() {}

    public static void main(String[] args) throws IOException, ParseException {
        String queryString = "people mountain people sea";

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        //You need to explicitly specify the ranking algorithm
        // using the respective Similarity class
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("TEXT", analyzer);
        Query query = parser.parse(queryString);
        TopDocs topDocs = searcher.search(query, 1000);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (int i = 0; i < scoreDocs.length; i++) {
            Document doc = searcher.doc(scoreDocs[i].doc);
            System.out.println(doc.get("DOCNO")+" "+scoreDocs[i].score);
        }
        reader.close();
    }
}
