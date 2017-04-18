package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.ui.activities.RecommendationsSetupActivity;

/**
 * Created by Sergey on 30.03.2017.
 */

public interface GetListTask {

  @GET("3/list/{id}")
  Call<RecommendationsSetupActivity.RetrofitResult> getListById(@Path("id") int id,
      @Query("api_key") String apiKey, @Query("language") String language);
}
