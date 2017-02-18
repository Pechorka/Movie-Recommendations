package ru.surf.course.movierecommendations.models;

import java.net.URL;

/**
 * Created by andrew on 2/18/17.
 */

public class Review {

    private int mId;
    private String mAuthor;
    private String mContent;
    private URL mURL;

    public Review(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public URL getURL() {
        return mURL;
    }

    public void setURL(URL URL) {
        mURL = URL;
    }
}
