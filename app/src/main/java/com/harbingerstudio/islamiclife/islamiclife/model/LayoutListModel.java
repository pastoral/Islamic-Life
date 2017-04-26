package com.harbingerstudio.islamiclife.islamiclife.model;

/**
 * Created by User on 4/15/2017.
 */

public class LayoutListModel {
    private String title;
    private int imageurl;

    public LayoutListModel(String title, int imageurl) {
        this.title = title;
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageurl() {
        return imageurl;
    }

    public void setImageurl(int imageurl) {
        this.imageurl = imageurl;
    }
}
