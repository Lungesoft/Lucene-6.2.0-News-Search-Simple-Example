package com.lungesoft.larch.web;

import com.lungesoft.larch.searcher.NewsSearch;
import com.lungesoft.larch.searcher.SearchException;
import com.lungesoft.larch.searcher.model.News;
import com.lungesoft.larch.searcher.model.ResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ControllerAPI {

    @Autowired
    private NewsSearch luceneSearch;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseList<News> getResultSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                              @RequestBody SelectionParams selections) {
        try {
            return luceneSearch.search(query, selections.getSelections());
        } catch (SearchException e) {
            return new ResponseList<>(0, 0, new ArrayList<>());
        }
    }
}
