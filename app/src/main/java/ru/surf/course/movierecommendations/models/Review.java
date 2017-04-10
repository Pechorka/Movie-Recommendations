package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.net.URL;

/**
 * Created by andrew on 2/18/17.
 */
@DatabaseTable(tableName = Review.TABLE_NAME_REVIEWS)
public class Review implements Serializable {


  public static final String TABLE_NAME_REVIEWS = "reviews";
  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_REVIEW_ID = "review_id";
  public static final String FIELD_NAME_AUTHOR = "author";
  public static final String FIELD_NAME_CONTENT = "content";
  public static final String FIELD_NAME_URL = "url";
  public static final String FIELD_NAME_MEDIA = "media";

  @DatabaseField(columnName = FIELD_NAME_REVIEW_ID)
  @SerializedName("id")
  private String mReviewId;

  @DatabaseField(columnName = FIELD_NAME_AUTHOR)
  @SerializedName("author")
  private String mAuthor;

  @DatabaseField(columnName = FIELD_NAME_CONTENT)
  @SerializedName("content")
  private String mContent;

  @DatabaseField(columnName = FIELD_NAME_URL)
  @SerializedName("url")
  private URL mURL;

  @DatabaseField(columnName = FIELD_NAME_MEDIA,foreign = true)
  private Media mMedia;

  @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
  private int mId;


  public Review(String id) {
    mReviewId = id;
  }

  public String getReviewId() {
    return mReviewId;
  }

  public void setReviewId(String id) {
    mReviewId = id;
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

  public Media getmMedia() {
    return mMedia;
  }

  public void setmMedia(Media mMedia) {
    this.mMedia = mMedia;
  }

  public int getmId() {
    return mId;
  }

  public void setmId(int mId) {
    this.mId = mId;
  }
}
