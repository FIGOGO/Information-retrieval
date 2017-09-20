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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static com.sun.org.apache.xml.internal.serialize.Method.TEXT;

public class generateIndex {

    public Document getDocument(File file) {
        Document luceneDoc = new Document();

        //luceneDoc.add(new StringField("DOCNO", DOCNO, Field.Store.YES));
        //luceneDoc.add(new TextField("TEXT", TEXT, Field.Store.YES));

        Field docnoField = new StringField();
        luceneDoc.add(docnoField);

        return luceneDoc;
    }

    public void createIndex(String corpusPath) throws IOException{
        Directory dir = FSDirectory.open(Paths. get (corpusPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        File[] files = new File(corpusPath).listFiles();
        for (File file : files) {
            Document luceneDoc = getDocument(file);
            writer.addDocument(luceneDoc);
        }
        writer.close();
    }

    public static void main(String[] args) {
        generateIndex generater = new generateIndex();
        generater.createIndex();
    }

}
