import org.apache.commons.lang3.StringUtils;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class generateIndex {
    String indexPath;
    String corpusPath;

    public generateIndex(){
        indexPath = "/Users/yansong/Programming/search/SONG-information-retrevial/assignment1/index";
        corpusPath = "/Users/yansong/Programming/search/SONG-information-retrevial/assignment1/corpus";
    }


    // Parse a file into multiple documents
    public ArrayList<Document> getDocument(File file) throws IOException {
        ArrayList<Document> luceneDocs = new ArrayList<>();
        String parseFileContent = new String(Files.readAllBytes(file.toPath()));
        String[] document = StringUtils.substringsBetween(parseFileContent, "<DOC>", "</DOC>");

        for (String doc : document) {
            Document lucenedoc = new Document();
            String[] docnoArray = StringUtils.substringsBetween(doc, "<DOCNO>", "</DOCNO>");
            String[] textArray = StringUtils.substringsBetween(doc, "<TEXT>", "</TEXT>");
            String[] bylineArray = StringUtils.substringsBetween(doc, "<BYLINE>", "</BYLINE>");
            String[] datelineArray = StringUtils.substringsBetween(doc, "<DATELINE>", "</DATELINE>");
            String[] headArray = StringUtils.substringsBetween(doc, "<HEAD>", "</HEAD>");
            String docno = Arrays.toString(docnoArray);
            String text = Arrays.toString(textArray);
            String byline = Arrays.toString(bylineArray);
            String dateline = Arrays.toString(datelineArray);
            String head = Arrays.toString(headArray);
            lucenedoc.add(new StringField("DOCNO", docno, Field.Store.YES));
            lucenedoc.add(new TextField("TEXT", text, Field.Store.YES));
            lucenedoc.add(new TextField("BYLINE", byline, Field.Store.YES));
            lucenedoc.add(new TextField("DATELINE", dateline, Field.Store.YES));
            lucenedoc.add(new TextField("HEAD", head, Field.Store.YES));
            luceneDocs.add(lucenedoc);
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
        writer.forceMerge(1);
        writer.commit();
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
