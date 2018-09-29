package com.lungesoft.larch.searcher.lucene;

import com.lungesoft.larch.searcher.NewsSearch;
import com.lungesoft.larch.searcher.SearchException;
import com.lungesoft.larch.searcher.model.News;
import com.lungesoft.larch.searcher.model.ResponseList;
import com.lungesoft.larch.utils.Utils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LuceneNewsSearch implements NewsSearch {

    private final Indexer indexer;
    private final Searcher searcher;

    public LuceneNewsSearch(String indexDir) throws SearchException {
        try {
            this.indexer = new Indexer(indexDir);
            this.searcher = new Searcher(indexDir);
        } catch (IOException e) {
            throw new SearchException(e);
        }
    }

    public void createIndex(String dataNewsDir) throws SearchException {
        try {
            System.out.println("Starting indexing");
            long startTime = System.currentTimeMillis();
            int numIndexed = indexer.createIndex(dataNewsDir);
            long endTime = System.currentTimeMillis();
            indexer.close();
            System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
        } catch (IOException e) {
            throw new SearchException(e);
        }
    }

    public ResponseList<News> search(String searchQuery, String... selections) throws SearchException {
        try {
            long startTime = System.currentTimeMillis();

            TopDocs hits = searcher.search(searchQuery, selections);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits + " documents with - " + searchQuery + " - found. Time :" + (endTime - startTime));

            ArrayList<News> newsList = new ArrayList<>();
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                String filepath = doc.get("filepath");
                News news = Utils.readFile(new File(filepath));
                if (news != null) {
                    news.setScore(scoreDoc.score);
                    newsList.add(news);
                }
                System.out.println("File: " + scoreDoc.score + " " + filepath);
            }
            return new ResponseList<>((endTime - startTime), hits.totalHits, newsList);
        } catch (IOException | ParseException e) {
            throw new SearchException(e);
        }
    }

}