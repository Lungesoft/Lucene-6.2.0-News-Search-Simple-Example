package com.lungesoft.larch.utils;

import com.lungesoft.larch.searcher.model.News;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utils {

    public static News readFile(File file) {
        StringBuilder stringJSON = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                stringJSON.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        JSONObject newsJson = new JSONObject(stringJSON.toString());
        if (newsJson.has("title")
                && newsJson.has("author")
                && newsJson.has("link")
                && newsJson.has("submit")
                && newsJson.has("text")) {
            News news = new News();
            news.setTitle(newsJson.getString("title"));
            news.setAuthor(newsJson.getString("author"));
            news.setLink(newsJson.getString("link"));
            news.setSubmit(newsJson.getString("submit"));
            news.setText(newsJson.getString("text"));
            news.setFilePath(file.getPath());
            return news;
        } else {
            return null;
        }
    }

}
