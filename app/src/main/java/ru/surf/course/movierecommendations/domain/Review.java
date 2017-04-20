package ru.surf.course.movierecommendations.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import lombok.Data;

/**
 * Created by andrew on 2/18/17.
 */
@Data
public class Review implements Serializable {

  @SerializedName("id")
  private String reviewId;

  @SerializedName("author")
  private String author;

  @SerializedName("content")
  private String content;

  @SerializedName("url")
  private URL url;

  public Review(String id) {
    reviewId = id;
  }

  public static class RetrofitResult {

    @SerializedName("results")
    public List<Review> reviews;
  }

}
