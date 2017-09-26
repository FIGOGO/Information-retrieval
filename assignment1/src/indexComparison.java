import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yansong on 9/24/17.
 */
public class indexComparison {
    public static void compare(generateIndex g, Analyzer a, String aType) throws Exception {
        g.createIndex(a, aType);
        Stats.main(null);
        System.out.println("\n");
    }

    public static void main(String[] args) throws Exception {
        KeywordAnalyzer keyWord = new KeywordAnalyzer();
        SimpleAnalyzer simple = new SimpleAnalyzer();
        StopAnalyzer stop = new StopAnalyzer();
        StandardAnalyzer standard = new StandardAnalyzer();

        generateIndex generator = new generateIndex();
        compare(generator, keyWord, "Key Word Analyzer");
        compare(generator, simple, "Simple Analyzer");
        compare(generator, stop, "Stop Analyzer");
        compare(generator, standard, "Standard Analyzer");
    }
}
