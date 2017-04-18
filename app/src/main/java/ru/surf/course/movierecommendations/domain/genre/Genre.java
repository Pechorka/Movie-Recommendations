package ru.surf.course.movierecommendations.domain.genre;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

/**
 * Created by andrew on 2/18/17.
 */


public class Genre implements Serializable {

  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_GENRE_ID = "genre_id";
  public static final String FIELD_NAME_GENRE_NAME = "genre_name";
  public static final String FIELD_NAME_CHECKED = "checked";

  @DatabaseField(id = true, columnName = FIELD_NAME_ID)
  private int dbId;

  @DatabaseField(columnName = FIELD_NAME_GENRE_ID)
  @SerializedName("id")
  private int mGenreId;

  @DatabaseField(columnName = FIELD_NAME_GENRE_NAME)
  @SerializedName("name")
  private String mName;

  @DatabaseField(columnName = FIELD_NAME_CHECKED, dataType = DataType.BOOLEAN)
  private boolean checked;

  public Genre() {
  }


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

  public int getId() {
    return dbId;
  }

  public void setId(int id) {
    this.dbId = id;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public void reverseChecked() {
    checked = !checked;
  }
}
