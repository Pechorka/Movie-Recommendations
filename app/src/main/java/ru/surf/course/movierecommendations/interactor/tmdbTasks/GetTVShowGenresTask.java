package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.interactor.TVShowGenre;
import rx.Observable;


public interface GetTVShowGenresTask {

    @GET("genre/{whose}/list")
    Observable<TVShowGenre.RetrofitResult> getGenres(@Path("whose") String mediaType,
                                               @Query("api_key") String apiKey);
}
