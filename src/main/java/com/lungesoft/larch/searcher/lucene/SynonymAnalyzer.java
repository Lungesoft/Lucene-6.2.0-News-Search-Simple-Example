package com.lungesoft.larch.searcher.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ru.RussianLightStemFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.IOException;

public class SynonymAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String s) {

        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        builder.add(new CharsRef("iphone"), new CharsRef("айфон"), true);

        SynonymMap mySynonymMap;
        try {
            mySynonymMap = builder.build();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        Tokenizer source = new ClassicTokenizer();
        TokenStream filter = new StandardFilter(source);
        filter = new LowerCaseFilter(filter);

        filter = new RussianLightStemFilter(filter);
        filter = new SynonymFilter(filter, mySynonymMap, true);

        return new TokenStreamComponents(source, filter);
    }

}
