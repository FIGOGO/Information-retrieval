import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yansong on 10/19/17.
 */
public class ExtractQueryFromTopics {

    ArrayList<String> extractQuery (String filePath, String beginTag, String endTag) throws IOException {
        ArrayList <String> topics = new ArrayList<>();
        File f = new File(filePath);
        System.out.println(f.toPath());
        String fileContent = new String(Files.readAllBytes(f.toPath()));
        String[] rawTopics = StringUtils.substringsBetween(fileContent, beginTag, endTag);
        for (String s : rawTopics) {
            s = StringUtils.substringAfter(s, "Topic:");
            topics.add(s);
        }
        /*
        for (String s : topics) {
            System.out.println(s);
        }
        */

        return topics;
    }

    public static void main(String[] args) throws IOException {
        ExtractQueryFromTopics extractor = new ExtractQueryFromTopics();
        extractor.extractQuery("/Users/yansong/Programming/search" +
                "/SONG-information-retrevial/assignment2/topics/topics.51-100", "<title>", "\n");
    }
}
