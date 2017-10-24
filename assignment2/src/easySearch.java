import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.lang.reflect.Array;
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

    public static double computeScore(int tf, double len, int N, int df) {
        return Math.log(1+N/df) * tf / len;
    }

    public static ArrayList<ScoreDocument> searchScore(Set<Term> set, IndexReader reader) throws IOException {
        int N = reader.numDocs();  // N
        ArrayList<ScoreDocument> sdArray = new ArrayList<>(N);
        List<LeafReaderContext> leafContextList = reader.leaves();
        ClassicSimilarity dSimi = new ClassicSimilarity();
        int index = 0;
        for (LeafReaderContext leaf : leafContextList) {
            LeafReader leafReader = leaf.reader();
            int numberOfDoc = leaf.reader().numDocs();
            for (int docId = 0; docId < numberOfDoc; docId++) {
                // Get normalized length (1/sqrt(numOfTokens)) of the document
                float normDocLeng = dSimi.decodeNormValue(leaf.reader()
                        .getNormValues("TEXT").get(docId));
                // Get length of the document
                float docLeng = 1 / (normDocLeng * normDocLeng);
                Document d = leafReader.document(docId);
                sdArray.add(index++, new ScoreDocument(d, docLeng));  // docLeng
            }
        }
        for (Term queryT : set) {
            // Document frequency
            int df = reader.docFreq(queryT);  // df
            for (LeafReaderContext leaf : leafContextList) {
                int startDocNo = leaf.docBase;
                // Get frequency of the term queryT from its postings
                PostingsEnum de = MultiFields.getTermDocsEnum(leaf.reader(),
                        "TEXT", new BytesRef(queryT.text()));
                int doc;
                if (de != null) {
                    while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
                        ScoreDocument sd = sdArray.get(de.docID()+startDocNo);
                        //System.out.println(de.docID());
                        sd.addScore(easySearch.computeScore(de.freq(), sd.getDocLength(), N, df));
                        //System.out.println(queryT.toString() + "occurs " + de.freq() + " time(s) in doc(" + de.docID()  + ")");
                    }
                }
            }
        }
        return sdArray;
    }

    public static void main(String[] args) throws ParseException, IOException {
        String queryString = "police people mountain people sea the";
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

        ArrayList<ScoreDocument> sdArray = easySearch.searchScore(queryTerms, reader);

        PriorityQueue<ScoreDocument> pq = new PriorityQueue<>();
        pq.addAll(sdArray);
        for (int i = 0; i < reader.numDocs(); i++){
            ScoreDocument sd = pq.poll();
            String docno = sd.getDoc().get("DOCNO");
            System.out.println("Document " + docno + " get score " + sd.getScore());
            if (sd.getScore() == 0) {
                System.out.println("In total "+(i+1)+" documents have positive evaluation score.");
                break;
            }
        }
    }
}
