package ru.surf.course.movierecommendations.interactor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import ru.surf.course.movierecommendations.domain.Media.MediaType;

/**
 * Created by sergey on 04.04.17.
 */
@Data
@DatabaseTable(tableName = CustomFilter.TABLE_NAME_CUSTOM_FILTER)
public class CustomFilter {

  public static final String TABLE_NAME_CUSTOM_FILTER = "custom_filter";
  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_FILTER_NAME = "filter_name";
  public static final String FIELD_NAME_GENRES_IDS = "genre_ids";
  public static final String FIELD_NAME_SORT_TYPE = "sort_type";
  public static final String FIELD_NAME_SORT_DIRECTION = "sort_direction";
  public static final String FIELD_NAME_MIN_YEAR = "min_year";
  public static final String FIELD_NAME_MAX_YEAR = "max_year";
  public static final String FIELD_NAME_MEDIA_TYPE = "media_type";
  @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
  private int id;
  @DatabaseField(columnName = FIELD_NAME_FILTER_NAME)
  private String filterName;
  @DatabaseField(columnName = FIELD_NAME_GENRES_IDS)
  private String genreIds;
  @DatabaseField(columnName = FIELD_NAME_SORT_TYPE)
  private String sortType;
  @DatabaseField(columnName = FIELD_NAME_SORT_DIRECTION)
  private String sortDirection;
  @DatabaseField(columnName = FIELD_NAME_MIN_YEAR)
  private String minYear;
  @DatabaseField(columnName = FIELD_NAME_MAX_YEAR)
  private String maxYear;
  @DatabaseField(columnName = FIELD_NAME_MEDIA_TYPE)
  private MediaType mediaType;

  public CustomFilter() {
  }

  public CustomFilter(String filterName, String genreIds, String sortType, String sortDirection,
      String minYear, String maxYear, MediaType mediaType) {
    this.filterName = filterName;
    this.genreIds = genreIds;
    this.sortType = sortType;
    this.sortDirection = sortDirection;
    this.minYear = minYear;
    this.maxYear = maxYear;
    this.mediaType = mediaType;
  }
}
