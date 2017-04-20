package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.interactor.MovieGenre;

/**
 * Created by sergey on 14.04.17.
 */

public interface GetMovieGenresTask {

  @GET("genre/{whose}/list")
  Call<MovieGenre.RetrofitResult> getGenres(@Path("whose") String mediaType,
      @Query("api_key") String apiKey);
}
