import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Created by yansong on 10/19/17.
 */
public class ExtractQueryFromTopics {
    public static final String pathToIndex = "/Users/yansong/Programming/search/SONG-information-retrevial/assignment2/index";

    ArrayList<Set <Term>> extractQuery (String filePath, String beginTag, String endTag) throws IOException, ParseException {
        ArrayList<String> topics = new ArrayList<>();
        // Open the topic file
        File f = new File(filePath);
        // Extract contents from the file
        String fileContent = new String(Files.readAllBytes(f.toPath()));
        // Extract raw topics form the contents
        String[] rawTopics = StringUtils.substringsBetween(fileContent, beginTag, endTag);
        // Add refined topics to List
        for (String s : rawTopics) {
            s = StringUtils.substringAfter(s, "Topic:");
            topics.add(s);
        }

        // Create index reader and searcher
        Directory indexDirectory = FSDirectory.open(Paths.get(pathToIndex));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Get the preprocessed query terms and add to queryListSet
        ArrayList<Set<Term>> queryListSet = new ArrayList<>();
        Analyzer analyzer = new StandardAnalyzer();
        for (String queryString : topics) {
            QueryParser parser = new QueryParser("TEXT", analyzer);
            Query query = parser.parse(QueryParser.escape(queryString));
            Set<Term> queryTerms = new LinkedHashSet<>();
            searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
            queryListSet.add(queryTerms);
        }

        return queryListSet;
    }

    public static void main(String[] args) {
        System.out.println("OK");
    }

}
