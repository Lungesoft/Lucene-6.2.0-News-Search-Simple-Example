package com.lungesoft.larch.searcher;

import com.lungesoft.larch.searcher.lucene.LuceneNewsSearch;

public class LuceneSimpleUsage {

    private static final String SEARCH_REQUEST = "айфон";

    public static void main(String[] args) {
        try {
            NewsSearch luceneSearch = new LuceneNewsSearch("index");
            luceneSearch.createIndex("data");
            luceneSearch.search(SEARCH_REQUEST, "author", "title", "text");
        } catch (SearchException e) {
            e.printStackTrace();
        }
    }
}
