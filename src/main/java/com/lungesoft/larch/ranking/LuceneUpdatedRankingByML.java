package com.lungesoft.larch.ranking;

import com.lungesoft.larch.searcher.lucene.Searcher;
import com.lungesoft.larch.searcher.lucene.SynonymAnalyzer;
import com.lungesoft.larch.searcher.model.News;
import com.lungesoft.larch.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class LuceneUpdatedRankingByML {

    private static final String REQUEST = "церемония";

    public static void main(String[] args) throws Exception {
        LuceneUpdatedRankingByML taskSearch = new LuceneUpdatedRankingByML();
        for (News news : taskSearch.doUpdateSearch(REQUEST)) {
            System.out.println(news + " " + Double.toString(news.getScore()));
        }
    }

    public LinearRegressionDataSet getLearnData() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(new File("ranking/learn.txt")));
        List<Double> yy = new ArrayList<>();
        List<List<Double>> xx = new ArrayList<>();
        while (scanner.hasNext()) {
            String[] arguments = scanner.nextLine().split(" : ");
            List<Double> x = new ArrayList<>();
            for (int i = 0; i < arguments.length - 3; i++) {
                x.add(Double.valueOf(arguments[i]));
            }
            xx.add(x);
            yy.add(Double.valueOf(arguments[arguments.length - 3]));
        }

        double[] y = yy.stream().mapToDouble(i -> i).toArray();

        double[][] x = new double[y.length][];
        IntStream.range(0, xx.size()).forEach(i -> x[i] = xx.get(i).stream().mapToDouble(j -> j).toArray());

        return new LinearRegressionDataSet(x, y);
    }

    public double[] learn() throws Exception {
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        LinearRegressionDataSet data = getLearnData();
        regression.newSampleData(data.getY(), data.getX());
        return regression.estimateRegressionParameters();
    }

    public List<News> doUpdateSearch(String request) throws Exception {
        List<News> newses = doUsualSearch(request);
        double[] coefficients = learn();
        newses.sort(Comparator.comparingDouble(news -> calcNewsRel(coefficients, calcNewsFactors(news, request))));
        return newses;
    }

    private double calcNewsRel(double[] coefficients, double[] factors) {
        double rel = coefficients[0];
        for (int i = 1; i < coefficients.length; i++) {
            rel += factors[i - 1] * coefficients[i];
        }
        return rel;
    }

    private double[] calcNewsFactors(News news, String request) {
        double firstFactor = 0;
        double secondFactor = 0;
        double thirdFactor = 0;
        double foursFactor = 0;

        for (String r : parseStemRequest(request)) {
            int firstFactorPrepare = StringUtils.countMatches(news.getTitle().toLowerCase(), r);
            if (firstFactorPrepare != 0) {
                firstFactor = firstFactorPrepare;
            }
            int secondFactorPrepare = StringUtils.countMatches(news.getSubmit().toLowerCase(), r);
            if (secondFactorPrepare != 0) {
                secondFactor = secondFactorPrepare;
            }
            int thirdFactorPrepare = StringUtils.countMatches(news.getText().toLowerCase(), r);
            if (thirdFactorPrepare != 0) {
                thirdFactor = thirdFactorPrepare;
            }
            foursFactor = news.getScore();
        }
        return new double[]{firstFactor, secondFactor, thirdFactor, foursFactor};
    }


    public List<News> doUsualSearch(String request) throws IOException, ParseException {
        Searcher searcher = new Searcher("index");
        TopDocs topDocs = searcher.search(request, "author", "title", "text", "submit");

        ArrayList<News> newses = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            String filepath = doc.get("filepath");
            News news = Utils.readFile(new File(filepath));
            if (news != null) {
                news.setScore(scoreDoc.score);
            }
            newses.add(news);
        }
        return newses;
    }

    public List<String> parseStemRequest(String keywords) {
        List<String> result = new ArrayList<>();
        TokenStream stream = new SynonymAnalyzer().tokenStream("", new StringReader(keywords));
        CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);

        try {
            stream.reset();
            while (stream.incrementToken()) {
                String term = charTermAttribute.toString();
                result.add(term);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
