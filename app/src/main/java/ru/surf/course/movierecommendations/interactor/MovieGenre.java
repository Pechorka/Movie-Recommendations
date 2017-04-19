package ru.surf.course.movierecommendations.interactor;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.table.DatabaseTable;
import java.util.List;
import ru.surf.course.movierecommendations.domain.genre.Genre;

/**
 * Created by sergey on 14.04.17.
 */

@DatabaseTable(tableName = MovieGenre.TABLE_NAME_MOVIE_GENRES)
public class MovieGenre extends Genre {

  public static final String TABLE_NAME_MOVIE_GENRES = "movie_genres";

  public static class RetrofitResult {
    @SerializedName("genres")
    public List<MovieGenre> movieGenres;
  }
}
