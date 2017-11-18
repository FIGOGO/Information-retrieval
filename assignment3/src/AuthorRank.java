import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

import java.io.*;
import java.util.*;

/**
 * Created by yansong on 11/16/17.
 */
public class AuthorRank {
    final static String filename = "/Users/yansong/Programming" +
            "/search/SONG-information-retrevial/assignment3/author.net.txt";

    public static String getFilePath() {
        return filename;
    }

    public static void getGraph(String filename, DirectedSparseGraph<String, String > graph,
                                Map<String, String> vMap) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        StringTokenizer st = new StringTokenizer(line);
        System.out.println("begin to add " + st.nextToken());
        // add vertices
        int vcount =Integer.parseInt(st.nextToken());
        for (int i = 0; i < vcount; i++) {
            line = br.readLine();
            st = new StringTokenizer(line);
            String vID = st.nextToken();
            String authorID = st.nextToken();
            graph.addVertex(vID);
            vMap.put(vID, authorID.substring(1, authorID.length()-1));
        }

        // add edges
        line = br.readLine();
        st = new StringTokenizer(line);
        System.out.println("begin to add " + st.nextToken());
        int ecount = Integer.parseInt(st.nextToken());
        for (int i = 1; i <+ ecount; i++) {
            line = br.readLine();
            st = new StringTokenizer(line);
            String vFrom = st.nextToken();
            String vTo = st.nextToken();
            st.nextToken();
            Pair<String> pair = new Pair<>(vFrom, vTo);
            graph.addEdge(Integer.toString(i), pair);
        }
    }

    public static void main(String[] args) throws IOException {
        DirectedSparseGraph graph = new DirectedSparseGraph();
        HashMap<String, String> vMap = new HashMap<>();
        getGraph(filename, graph, vMap);

        double alpha = 0.1;
        PageRank<String, String> ranker = new PageRank<String, String>(graph, alpha);
        ranker.evaluate();

        // TreeMap with key decreasing order
        Map<Double, String> tMap = new TreeMap<>(Collections.reverseOrder());
        for (String v : vMap.keySet()) {
            double score = ranker.getVertexScore(v);
            tMap.put(score, vMap.get(v));
        }
        Set s = tMap.entrySet();
        Iterator iterator = s.iterator();
        for (int i = 0; i < 10; i++) {
            Map.Entry me = (Map.Entry) iterator.next();
            System.out.println("Author " + me.getValue() + " has score " + me.getKey());
        }
    }

}
