import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 * Created by yansong on 10/19/17.
 */
public class ExtractQueryFromTopics {
    public static final String pathToIndex = "/Users/yansong/Programming/search/SONG-information-retrevial/assignment2/index";

    public static ArrayList<Query> extractQuery (String filePath, String beginTag, String endTag, Analyzer analyzer)
            throws IOException, ParseException {
        ArrayList<String> topics = new ArrayList<>();
        // Open the topic file
        File f = new File(filePath);
        // Extract contents from the file
        String fileContent = new String(Files.readAllBytes(f.toPath()));
        // Extract raw topics form the contents
        String[] rawTopics = StringUtils.substringsBetween(fileContent, beginTag, endTag);
        // Add refined topics to List
        for (String s : rawTopics) {
            s = StringUtils.substringAfter(s, ":");
            topics.add(s);
        }

        // Get the preprocessed query terms and add to queryListSet
        ArrayList<Query> queryList = new ArrayList<>();
        for (String queryString : topics) {
            QueryParser parser = new QueryParser("TEXT", analyzer);
            Query query = parser.parse(QueryParser.escape(queryString));
            queryList.add(query);
        }
        return queryList;
    }

}
