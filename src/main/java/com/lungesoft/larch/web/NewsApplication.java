package com.lungesoft.larch.web;

import com.lungesoft.larch.searcher.NewsSearch;
import com.lungesoft.larch.searcher.SearchException;
import com.lungesoft.larch.searcher.lucene.LuceneNewsSearch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsApplication.class, args);
    }

    @Bean
    public NewsSearch load() throws SearchException {
        NewsSearch luceneSearch = new LuceneNewsSearch("index");
        luceneSearch.createIndex("data");
        return luceneSearch;
    }
}