package ru.surf.course.movierecommendations.interactor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import ru.surf.course.movierecommendations.domain.Media.MediaType;

/**
 * Created by Sergey on 26.03.2017.
 */
@Data
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

}
