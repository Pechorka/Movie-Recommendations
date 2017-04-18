package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.domain.TVShowGenre;

/**
 * Created by sergey on 14.04.17.
 */

public interface GetTVShowGenresTask {

  @GET("3/genre/{whose}/list")
  Call<TVShowGenre.RetrofitResult> getGenres(@Path("whose") String mediaType,
      @Query("api_key") String apiKey);
}
