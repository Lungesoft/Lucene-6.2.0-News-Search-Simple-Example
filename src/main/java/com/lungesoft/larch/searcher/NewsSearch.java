package com.lungesoft.larch.searcher;

import com.lungesoft.larch.searcher.model.News;
import com.lungesoft.larch.searcher.model.ResponseList;

public interface NewsSearch {
    void createIndex(String dataNewsDir) throws SearchException;

    ResponseList<News> search(String searchQuery, String... selections) throws SearchException;
}
