package com.lungesoft.larch.ranking;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ZipfLaw {

    private static TreeMap<String, Long> multiset = new TreeMap<>();
    private static ArrayList<String> fields = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        Directory indexDirectory = FSDirectory.open(new File("index").toPath());
        IndexReader reader = DirectoryReader.open(indexDirectory);
        MultiFields.getFields(reader).forEach(fields::add);

        TermsEnum termEnum = MultiFields.getTerms(reader, "contents").iterator();
        BytesRef bytesRef;
        while ((bytesRef = termEnum.next()) != null) {
            String term = bytesRef.utf8ToString();
            if (!fields.contains(term)) {
                multiset.put(term, termEnum.totalTermFreq());
            }
        }

        List<Map.Entry<String, Long>> l = new ArrayList<>(multiset.entrySet());
        l.sort((o1, o2) -> -1 * o1.getValue().compareTo(o2.getValue()));

        //Exel Result.csv -> create Diagram
        Writer writer = new OutputStreamWriter(new FileOutputStream(new File("Result.csv")), "windows-1251");
        String first = "Rank" + "," + "Word" + "," + "Freq" + "\n";
        writer.write(first);
        int rank = 1;
        for (Map.Entry<String, Long> e : l) {
            String res = rank + "," + String.format(Locale.ENGLISH, "%.6f", ((double) e.getValue() / multiset.size())) + "," + e.getKey() + "\n";
            writer.write(res);
            rank++;
        }
        writer.flush();
        writer.close();
    }
}