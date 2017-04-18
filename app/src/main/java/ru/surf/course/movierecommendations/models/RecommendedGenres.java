package ru.surf.course.movierecommendations.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Sergey on 30.03.2017.
 */

public class RecommendedGenres {

  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_GENRE_ID = "genre_id";

  @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
  private int id;

  @DatabaseField(columnName = FIELD_NAME_GENRE_ID)
  private int genre_id;

  public RecommendedGenres() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getGenre_id() {
    return genre_id;
  }

  public void setGenre_id(int genre_id) {
    this.genre_id = genre_id;
  }
}
