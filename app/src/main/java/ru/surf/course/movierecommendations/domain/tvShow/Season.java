package ru.surf.course.movierecommendations.domain.tvShow;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Created by Sergey on 16.02.2017.
 */
@Data
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


}
