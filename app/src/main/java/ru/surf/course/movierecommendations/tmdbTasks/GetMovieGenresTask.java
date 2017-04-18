package ru.surf.course.movierecommendations.tmdbTasks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.models.MovieGenre;

/**
 * Created by sergey on 14.04.17.
 */

public interface GetMovieGenresTask {

  @GET("3/genre/{whose}/list")
  Call<MovieGenre.RetrofitResult> getGenres(@Path("whose") String mediaType,
      @Query("api_key") String apiKey);
}