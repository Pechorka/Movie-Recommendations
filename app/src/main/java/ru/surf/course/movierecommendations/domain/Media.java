package ru.surf.course.movierecommendations.domain;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lombok.Data;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.people.Credit;

/**
 * Created by andrew on 08-Feb-17.
 */

@Data
public class Media implements Serializable {

  private final String TAG = getClass().getSimpleName();

  @SerializedName("title")
  protected String title;

  @SerializedName("original_title")
  protected String originalTitle;

  @SerializedName("original_language")
  protected Locale originalLanguage;

  @SerializedName("genres")
  protected ArrayList<Genre> genres;

  @SerializedName("genre_ids")
  protected ArrayList<Integer> genresIds;

  @SerializedName("poster_path")
  protected String posterPath;

  protected List<TmdbImage> backdrops;

  protected Bitmap posterBitmap;

  @SerializedName("overview")
  protected String overview;

  @SerializedName("release_date")
  protected Date date;

  @SerializedName("backdrop_path")
  protected String backdropPath;

  @SerializedName("vote_average")
  protected Double voteAverage;

  @SerializedName("vote_count")
  protected int voteCount;

  @SerializedName("id")
  protected int mediaId;

  @SerializedName("budget")
  protected String budget;

  @SerializedName("production_companies")
  protected ArrayList<ProductionCompanies> productionCompanies;

  @SerializedName("production_countries")
  protected ArrayList<ProductionCountries> productionCountries;

  protected Locale infoLanguage;

  @SerializedName("status")
  protected String status;

  @SerializedName("credits")
  protected ArrayList<Credit> credits;

  @SerializedName("reviews")
  protected ArrayList<Review> reviews;

  public Media(int mediaId) {
    this.mediaId = mediaId;
  }

  public void fillFields(Object from) {
    Field[] fields = from.getClass().getDeclaredFields();
    for (Field field : fields) {
      try {
        Field fieldFrom = from.getClass().getDeclaredField(field.getName());
        Object value = fieldFrom.get(from);
        this.getClass().getDeclaredField(field.getName()).set(this, value);
      } catch (IllegalAccessException e) {
        Log.d(TAG, "Copy error" + e.getMessage());
      } catch (NoSuchFieldException e) {
        Log.d(TAG, "Copy error" + e.getMessage());
      }
    }
  }

  public enum MediaType {
    movie,
    tv
  }
}