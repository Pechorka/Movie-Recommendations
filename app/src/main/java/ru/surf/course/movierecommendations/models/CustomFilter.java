package ru.surf.course.movierecommendations.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by sergey on 04.04.17.
 */

public class CustomFilter {

  public static final String TABLE_NAME_CUSTOM_FILTER = "custom_filter";

  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_FILTER_NAME = "filter_name";
  public static final String FIELD_NAME_GENRES_IDS = "genre_ids";
  public static final String FIELD_NAME_SORT = "sort";
  public static final String FIELD_NAME_MIN_YEAR = "min_year";
  public static final String FIELD_NAME_MAX_YEAR = "max_year";

  @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
  private int id;

  @DatabaseField(columnName = FIELD_NAME_FILTER_NAME)
  private String filterName;

  @DatabaseField(columnName = FIELD_NAME_GENRES_IDS)
  private String genreIds;

  @DatabaseField(columnName = FIELD_NAME_SORT)
  private String sort;

  @DatabaseField(columnName = FIELD_NAME_MIN_YEAR)
  private String minYear;

  @DatabaseField(columnName = FIELD_NAME_MAX_YEAR)
  private String maxYear;

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

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
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
}
