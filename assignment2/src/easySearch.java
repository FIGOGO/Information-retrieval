import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;


/**
 * Created by yansong on 10/5/17.
 *
 */
public class easySearch {
    final static String topicFilePath = "/Users/yansong/Programming/search" +
                "/SONG-information-retrevial/assignment2/topics/topics.51-100";
    final static String indexDirPath = "/Users/yansong/Programming/search" +
            "/SONG-information-retrevial/assignment2/index";

    public int setDocScore(IndexReader reader, Set<Term> querySet, ScoreDocument doc) {
        int score = 0;
        int N = reader.numDocs();
        for (Term queryTerm : querySet) {
        }
        return 0;
    }

    public static ArrayList<ScoreDocument> getAllDocuments (IndexReader reader) throws IOException {
        ArrayList<ScoreDocument> sdList = new ArrayList<>();

        return sdList;
    }

    public static void main(String[] args) throws ParseException, IOException {
        String queryString = "people mountain people sea";
        String pathToIndex = "./index";

        // Create index reader and searcher
        Directory indexDirectory = FSDirectory.open(Paths.get(pathToIndex));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Get the preprocessed query terms
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("TEXT", analyzer);
        Query query = parser.parse(queryString);
        Set<Term> queryTerms = new LinkedHashSet<>();
        searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);

        // Document frequency
        for (Term queryT : queryTerms) {
            int df = reader.docFreq(queryT);
            System.out.println("Number of documents containing the term " +
                    "\""+queryT.text()+"\" for field \"TEXT\": "+df);
        }

        List<LeafReaderContext> leafContextList = reader.leaves();
        ClassicSimilarity dSimi = new ClassicSimilarity();
        for (LeafReaderContext leaf : leafContextList) {
            LeafReader leafReader = leaf.reader();
            int startDocNo = leaf.docBase;
            int numberOfDoc = leaf.reader().numDocs();
            for (int docId = 0; docId < numberOfDoc; docId++) {
            // Get normalized length (1/sqrt(numOfTokens)) of the document
                float normDocLeng = dSimi.decodeNormValue(leaf.reader()
                        .getNormValues("TEXT").get(docId));
                // Get length of the document
                float docLeng = 1 / (normDocLeng * normDocLeng);
                sdList.add(new ScoreDocument(reader.document(docId+startDocNo), docLeng));
            }

           // Get frequency of the term "police" from its postings
           PostingsEnum de = MultiFields.getTermDocsEnum(leaf.reader(),
                   "TEXT", new BytesRef("police"));
           int doc;
           if (de != null) {
               while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
                   System.out.println("\"police\" occurs " + de.freq() + " time(s) in doc(" + de.docID()  + ")");
               }
           }
        }
    }
}
