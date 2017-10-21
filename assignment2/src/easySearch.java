import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by yansong on 10/5/17.
 *
 */
public class easySearch {

    public int getScore(Set<Term> querySet) {
        int score = 0;
        for (Term queryTerm : querySet) {
        }
        return score;
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

        // Document length and term frequency
        ClassicSimilarity dSimi = new ClassicSimilarity();
        // Get index leaf info
        List<LeafReaderContext> leafContextList = reader.leaves();
       for (LeafReaderContext leaf : leafContextList) {
           int docId = 0;
           // Get normalized length (1/sqrt(numOfTokens)) of the document
           float normDocLeng = dSimi.decodeNormValue(leaf.reader()
                   .getNormValues("TEXT").get(docId));
           // Get length of the document
           float docLeng = 1 / (normDocLeng * normDocLeng);

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
