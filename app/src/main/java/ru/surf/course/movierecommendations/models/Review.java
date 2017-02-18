package ru.surf.course.movierecommendations.models;

import java.net.URL;

/**
 * Created by andrew on 2/18/17.
 */

public class Review {

    private String mId;
    private String mAuthor;
    private String mContent;
    private URL mURL;

    public Review(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
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
