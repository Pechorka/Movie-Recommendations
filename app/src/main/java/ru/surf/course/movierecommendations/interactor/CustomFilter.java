package ru.surf.course.movierecommendations.interactor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.surf.course.movierecommendations.domain.MediaType;

/**
 * Created by sergey on 04.04.17.
 */

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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFilterName() {
    return filterName;
  }

  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }

  public String getGenreIds() {
    return genreIds;
  }

  public void setGenreIds(String genreIds) {
    this.genreIds = genreIds;
  }

  public String getSortType() {
    return sortType;
  }

  public void setSortType(String sortType) {
    this.sortType = sortType;
  }

  public String getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(String sortDirection) {
    this.sortDirection = sortDirection;
  }

  public String getMinYear() {
    return minYear;
  }

  public void setMinYear(String minYear) {
    this.minYear = minYear;
  }

  public String getMaxYear() {
    return maxYear;
  }

  public void setMaxYear(String maxYear) {
    this.maxYear = maxYear;
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  public void setMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
  }
}
