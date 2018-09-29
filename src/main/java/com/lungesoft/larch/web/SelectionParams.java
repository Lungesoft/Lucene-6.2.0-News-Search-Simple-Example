package com.lungesoft.larch.web;

import java.util.ArrayList;

public class SelectionParams {

    private boolean title;
    private boolean author;
    private boolean date;
    private boolean text;
    private boolean submit;

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean isAuthor() {
        return author;
    }

    public void setAuthor(boolean author) {
        this.author = author;
    }

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public boolean isSubmit() {
        return submit;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit;
    }

    public String[] getSelections() {
        ArrayList<String> listSelections = new ArrayList<>();
        if (this.title)
            listSelections.add("title");
        if (this.date)
            listSelections.add("author");
        if (this.author)
            listSelections.add("author");
        if (this.text)
            listSelections.add("text");
        if (this.submit)
            listSelections.add("submit");

        return listSelections.toArray(new String[0]);
    }
}
