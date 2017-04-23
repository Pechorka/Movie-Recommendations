package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.domain.people.Credit;
import rx.Observable;

/**
 * Created by sergey on 20.04.17.
 */

public interface GetCreditsTaskRetrofit {
  @GET("{whose}/{id}/credits")
  Observable<Credit.RetrofitResult> getMediaCreditsById(@Path("id") int id,
      @Path("whose") String whose,
      @Query("api_key") String apiKey);

  @GET("person/{id}/combined_credits")
  Observable<Credit.RetrofitResult> getpersonCreditsById(@Path("id") int id,
      @Query("api_key") String apiKey, @Query("language") String language);
}
