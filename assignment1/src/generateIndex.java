import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class generateIndex {
    String indexPath;
    String corpusPath;

    public generateIndex(){
        indexPath = "/Users/yansong/Programming/search/SONG-information-retrevial/assignment1/index";
        corpusPath = "/Users/yansong/Programming/search/SONG-information-retrevial/assignment1/corpus";
    }

    public HashMap<String, String> parseDOC(StringBuilder sb) {
        HashMap<String, String> doc = new HashMap<>();
        ArrayList<String[]> tags = new ArrayList<>();
        String[] docnoTag = {"DOCNO", "<DOCNO>", "</DOCNO>"};
        String[] headTag = {"HEAD", "<HEAD>", "</HEAD>"};
        String[] bylineTag = {"BYLINE", "<BYLINE>", "</BYLINE>"};
        String[] datelineTag = {"DATELINE", "<DATELINE>", "</DATELINE>"};
        String[] textTag = {"TEXT", "<TEXT>", "</TEXT>"};

        tags.add(docnoTag);
        tags.add(headTag);
        tags.add(bylineTag);
        tags.add(datelineTag);
        tags.add(textTag);

        for (String[] tagList: tags) {
            String nameTag = tagList[0];
            String startTag = tagList[1];
            String endTag = tagList[2];
            int start = sb.indexOf(startTag);
            int end = sb.indexOf(endTag);
            if (start == -1 || end == -1) {
                doc.put(nameTag, "");
            }
            else{
                doc.put(nameTag, sb.substring(start+nameTag.length(), end));
            }
        }
        return doc;
    }

    public ArrayList<HashMap<String, String>> parseFile(File file) throws IOException {
        // System.out.println(file.toPath());
        ArrayList<HashMap<String, String>> documents = new ArrayList<>();

        Scanner in = new Scanner(file);
        while(in.hasNext()) {
            try{
                String next = in.next();
                if (next.equals("<DOC>")) {
                    StringBuilder sb = new StringBuilder();
                    while (!in.hasNext("</DOC>")) {
                        String nextT = in.next();
                        sb.append(nextT+" ");
                    }
                documents.add(parseDOC(sb));
                }
            } catch (NoSuchElementException e){
                e.printStackTrace();
            }
        }
        return documents;
    }

    // Parse a file into multiple documents
    public ArrayList<Document> getDocument(File file) throws IOException {
        ArrayList<Document> luceneDocs = new ArrayList<>();
        ArrayList<HashMap<String, String>> documents = parseFile(file);

        for (HashMap<String, String> doc : documents) {
            Document luceneDoc = new Document();

            Field docnoField = new StringField("DOCNO", doc.get("DOCNO"), Field.Store.YES);
            Field datelineField = new TextField("DATELINE", doc.get("DATELINE"), Field.Store.YES);
            Field headField = new TextField("HEAD", doc.get("HEAD"), Field.Store.YES);
            Field bylineField = new TextField("BYLINE", doc.get("BYLINE"), Field.Store.YES);
            TextField textField = new TextField("TEXT", doc.get("TEXT"), Field.Store.YES);

            luceneDoc.add(docnoField);
            luceneDoc.add(datelineField);
            luceneDoc.add(headField);
            luceneDoc.add(bylineField);
            luceneDoc.add(textField);

            luceneDocs.add(luceneDoc);
        }

        return luceneDocs;
    }

    public void createIndex(Analyzer analyzer, String analyzerType) throws IOException{
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);
        System.out.println("Begin to generate index for " + analyzerType);

        File[] files = new File(corpusPath).listFiles();
        for (File file : files) {
            ArrayList<Document> luceneDocs = getDocument(file);
            for (Document luceneDoc : luceneDocs){
                writer.addDocument(luceneDoc);
            }
        }
        writer.close();
    }

    public static void main(String[] args) {
        try{
            generateIndex generator = new generateIndex();
            generator.createIndex(new StandardAnalyzer(), "Standard Analyzer");
            System.out.println("Done for the index");
            System.out.println("Begin the statistics");
            Stats.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
