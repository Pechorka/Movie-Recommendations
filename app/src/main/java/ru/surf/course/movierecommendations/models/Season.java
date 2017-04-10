package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Sergey on 16.02.2017.
 */
@DatabaseTable(tableName = Season.TABLE_NAME_SEASONS)
public class Season implements Serializable {

  public static final String TABLE_NAME_SEASONS = "seasons_table";
  public static final String FIELD_NAME_TV_SHOW_INFO = "tv_show_info";
  public static final String FIELD_NAME_AIR_DATE = "air_date";
  public static final String FIELD_NAME_EPISODE_COUNT = "episode_count";
  public static final String FIELD_NAME_SEASON_ID = "season_id";
  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_POSTER_PATH = "poster_path";
  public static final String FIELD_NAME_SEASON_NUMBER = "season_number";

  @DatabaseField(columnName = FIELD_NAME_AIR_DATE)
  @SerializedName("air_date")
  private Date airDate;

  @DatabaseField(columnName = FIELD_NAME_EPISODE_COUNT)
  @SerializedName("episode_count")
  private int episodeCount;

  @DatabaseField(columnName = FIELD_NAME_SEASON_ID)
  @SerializedName("id")
  private int seasonId;

  @DatabaseField(columnName = FIELD_NAME_POSTER_PATH)
  @SerializedName("poster_path")
  private String posterPath;

  @DatabaseField(columnName = FIELD_NAME_SEASON_NUMBER)
  @SerializedName("season_number")
  private int seasonNumber;

  @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
  private int id;

  @DatabaseField(columnName = FIELD_NAME_TV_SHOW_INFO,foreign = true)
  private TVShowInfo tvShowInfo;

  public Date getAirDate() {
    return airDate;
  }

  public void setAirDate(Date airDate) {
    this.airDate = airDate;
  }

  public int getEpisodeCount() {
    return episodeCount;
  }

  public void setEpisodeCount(int episodeCount) {
    this.episodeCount = episodeCount;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(int seasonId) {
    this.seasonId = seasonId;
  }

  public String getPosterPath() {
    return posterPath;
  }

  public void setPosterPath(String posterPath) {
    this.posterPath = posterPath;
  }

  public int getSeasonNumber() {
    return seasonNumber;
  }

  public void setSeasonNumber(int seasonNumber) {
    this.seasonNumber = seasonNumber;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public TVShowInfo getTvShowInfo() {
    return tvShowInfo;
  }

  public void setTvShowInfo(TVShowInfo tvShowInfo) {
    this.tvShowInfo = tvShowInfo;
  }
}
