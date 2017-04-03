package ru.surf.course.movierecommendations.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Sergey on 26.03.2017.
 */

@DatabaseTable(tableName = Favorite.TABLE_NAME_FAVORITES)
public class Favorite {

  public static final String TABLE_NAME_FAVORITES = "favorites";

  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_TITLE = "title";
  public static final String FIELD_NAME_MEDIA_ID = "media_id";
  public static final String FILED_NAME_POSTER_PATH = "poster_path";
  public static final String FIELD_NAME_MEDIA_TYPE = "media_type";

  @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
  private int id;

  @DatabaseField(columnName = FIELD_NAME_TITLE)
  private String title;

  @DatabaseField(columnName = FIELD_NAME_MEDIA_ID)
  private int mediaId;

  @DatabaseField(columnName = FILED_NAME_POSTER_PATH)
  private String posterPath;

  @DatabaseField(columnName = FIELD_NAME_MEDIA_TYPE)
  private MediaType mediaType;

  public Favorite() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getMediaId() {
    return mediaId;
  }

  public void setMediaId(int mediaId) {
    this.mediaId = mediaId;
  }

  public String getPosterPath() {
    return posterPath;
  }

  public void setPosterPath(String posterPath) {
    this.posterPath = posterPath;
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  public void setMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
  }
}
