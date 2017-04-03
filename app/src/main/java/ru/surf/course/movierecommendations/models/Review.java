package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.net.URL;

/**
 * Created by andrew on 2/18/17.
 */

public class Review implements Serializable {

  @SerializedName("id")
  private String mId;
  @SerializedName("author")
  private String mAuthor;
  @SerializedName("content")
  private String mContent;
  @SerializedName("url")
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
