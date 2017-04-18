package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Sergey on 16.02.2017.
 */

public class Season implements Serializable {

  @SerializedName("air_date")
  private Date airDate;

  @SerializedName("episode_count")
  private int episodeCount;

  @SerializedName("id")
  private int seasonId;

  @SerializedName("poster_path")
  private String posterPath;

  @SerializedName("season_number")
  private int seasonNumber;


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

}
