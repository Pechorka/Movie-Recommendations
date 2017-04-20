package ru.surf.course.movierecommendations.domain.genre;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;
import lombok.Data;

/**
 * Created by andrew on 2/18/17.
 */

@Data
public class Genre implements Serializable {

  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_GENRE_ID = "genre_id";
  public static final String FIELD_NAME_GENRE_NAME = "genre_name";
  public static final String FIELD_NAME_CHECKED = "checked";

  @DatabaseField(id = true, columnName = FIELD_NAME_ID)
  private int dbId;

  @DatabaseField(columnName = FIELD_NAME_GENRE_ID)
  @SerializedName("id")
  private int genreId;

  @DatabaseField(columnName = FIELD_NAME_GENRE_NAME)
  @SerializedName("name")
  private String name;

  @DatabaseField(columnName = FIELD_NAME_CHECKED, dataType = DataType.BOOLEAN)
  private boolean checked;

  public Genre() {
  }


  public Genre(int id, String name) {
    genreId = id;
    this.name = name;
  }

  public Genre(int id) {
    genreId = id;
  }

  public void reverseChecked() {
    checked = !checked;
  }
}
