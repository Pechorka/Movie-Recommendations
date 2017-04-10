package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

/**
 * Created by andrew on 2/18/17.
 */

@DatabaseTable(tableName = Genre.TABLE_NAME_GENRES)
public class Genre implements Serializable {


  public static final String TABLE_NAME_GENRES = "genres";
  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_GENRE_ID = "genre_id";
  public static final String FIELD_NAME_GENRE_NAME = "genre_name";
  public static final String FIELD_NAME_MEDIA = "media";

  @DatabaseField(columnName = FIELD_NAME_GENRE_ID)
  @SerializedName("id")
  private int mGenreId;

  @DatabaseField(columnName = FIELD_NAME_GENRE_NAME)
  @SerializedName("name")
  private String mName;

  @DatabaseField(columnName = FIELD_NAME_MEDIA,foreign = true)
  private Media mMedia;

  @DatabaseField(columnName = FIELD_NAME_ID)
  private int mId;

  public Genre(int id, String name) {
    mGenreId = id;
    mName = name;
  }

  public Genre(int id) {
    mGenreId = id;
  }

  public int getGenreId() {
    return mGenreId;
  }

  public void setGenreId(int id) {
    mGenreId = id;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
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
