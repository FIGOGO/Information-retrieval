import java.nio.file.Paths;

import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class Stats {
	public static void main(String[] args) throws Exception {
		String index = "/Users/yansong/Programming/search/SONG-information-retrevial/assignment1/index";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));

		System.out.println("Total number of documents in the corpus: "
				+ reader.maxDoc());

        //Print the number of documents containing the term "new" in <field>TEXT</field>.
        System.out.println("Number of documents containing the term " +
                "\"new\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "new")));

        //Print the total number of occurrences of the term "new"
        // across all documents for <field>TEXT</field>.
        System.out.println("Number of occurrences of \"new\" in " +
                "the field \"TEXT\": "+reader.totalTermFreq(new Term("TEXT","new")));


        Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
        //Print the size of the vocabulary for <field>TEXT</field>,
        // applicable when the index has only one segment.
		System.out.println("Size of the vocabulary for this field: "
				+ vocabulary.size());

        //Print the total number of documents that have at least one term for <field>TEXT</field>
        System.out
				.println("Number of documents that have at least one term for this field: "
						+ vocabulary.getDocCount());

        //Print the total number of tokens for <field>TEXT</field>
        System.out.println("Number of tokens for this field: "
				+ vocabulary.getSumTotalTermFreq());

        //Print the total number of postings for <field>TEXT</field>
        System.out.println("Number of postings for this field: "
				+ vocabulary.getSumDocFreq());

		/*
		TermsEnum iterator = vocabulary.iterator();
		BytesRef byteRef = null;
		System.out.println("\n*******Vocabulary-Start**********");
		while ((byteRef = iterator.next()) != null) {
			String term = byteRef.utf8ToString();
			System.out.println(term);
		}
		System.out.println("\n*******Vocabulary-End**********");
		*/

		reader.close();
	}
}
