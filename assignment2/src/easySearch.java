import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created by yansong on 10/5/17.
 *
 */
public class easySearch {

    public static void main(String[] args) throws ParseException, IOException {
        String queryString = "people mountain people sea";
        String pathToIndex = "./index";

        // Get the preprocessed query terms
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("TEXT", analyzer);
        Query query = parser.parse(queryString);
        Directory indexDirectory = FSDirectory.open(Paths.get(pathToIndex));
        IndexReader reader = DirectoryReader.open(indexDirectory);

        //Use DefaultSimilarity.decodeNormValue(…) to decode normalized document length
        Similarity dSimi = new ClassicSimilarity();

        //Get the segments of the index
        List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();

        for (int i = 0; i < leafContexts.size(); i++) {
            LeafReaderContext leafContext = leafContexts.get(i);
            int startDocNo = leafContext.docBase;
            int numberOfDoc = leafContext.reader().maxDoc();

            for (int docId = startDocNo; docId < startDocNo + numberOfDoc; docId++) {
                /*
                //Get normalized length for each document
                float normDocLeng = dSimi.decodeNormValue(leafContext.reader()
                        .getNormValues("TEXT").get(docId - startDocNo));
                System.out.println("Normalized length for doc(" + docId + ") is " + normDocLeng);
                */
            }
        }

    /*
        //Get the term frequency of "new" within each document containing it for <field>TEXT</field>
        DocsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
                MultiFields.getLiveDocs(leafContext.reader()), "TEXT", new BytesRef("new"));
        int doc;
        while ((doc = de.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
            System.out.println("\"new\" occurs "+de.freq() + " times in doc(" + (de.docID()+startDocNo)+") for the field TEXT");
        }
        }
        System.out.println(leafContexts);



        Set<Term> queryTerms = new LinkedHashSet<Term>();
        query.extractTerms(queryTerms);
	    for (Term t : queryTerms) {
            System.out.println(t.text());
        }

        //Use DefaultSimilarity.decodeNormValue(…) to decode normalized document length
        DefaultSimilarity dSimi=new DefaultSimilarity();

        //Get the segments of the index
        List<AtomicReaderContext> leafContexts = reader.getContext().reader().leaves();


    */
    }
}
