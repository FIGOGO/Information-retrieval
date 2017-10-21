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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.nio.file.Paths.*;

/**
 * Created by yansong on 10/5/17.
 */
public class searchTRECtopics {
    final static String topicFilePath = "/Users/yansong/Programming/search" +
                "/SONG-information-retrevial/assignment2/topics/topics.51-100";
    final static String indexDirPath = "/Users/yansong/Programming/search" +
            "/SONG-information-retrevial/assignment2/index";

    public static void main(String[] args) throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(get(indexDirPath));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new StandardAnalyzer();
        ExtractQueryFromTopics extractor = new ExtractQueryFromTopics();
        ArrayList<Query> queryList = extractor.extractQuery
                (topicFilePath, "<title>", "\n", analyzer);

        ArrayList<Set<Term>> queryListSet = new ArrayList<>();
        for (Query q : queryList) {
            Set<Term> queryTerms = new LinkedHashSet<>();
            searcher.createNormalizedWeight(q, false).extractTerms(queryTerms);
            queryListSet.add(queryTerms);
        }
    }
}
