package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.ui.screen.recommendationsSetup.RecommendationsSetupActivityPresenter;


public interface GetListTask {

    @GET("list/{id}")
    Call<RecommendationsSetupActivityPresenter.RetrofitResult> getListById(@Path("id") int id,
                                                                           @Query("api_key") String apiKey, @Query("language") String language);
}
