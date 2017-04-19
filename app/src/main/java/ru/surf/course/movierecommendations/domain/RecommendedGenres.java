package ru.surf.course.movierecommendations.domain;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;

/**
 * Created by Sergey on 30.03.2017.
 */
@Data
public class RecommendedGenres {

  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_GENRE_ID = "genre_id";

  @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
  private int id;

  @DatabaseField(columnName = FIELD_NAME_GENRE_ID)
  private int genreId;

  public RecommendedGenres() {
  }

}
