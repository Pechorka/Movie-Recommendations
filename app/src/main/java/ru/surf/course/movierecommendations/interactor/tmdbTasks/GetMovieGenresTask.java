package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.interactor.MovieGenre;
import rx.Observable;


public interface GetMovieGenresTask {

    @GET("genre/{whose}/list")
    Observable<MovieGenre.RetrofitResult> getGenres(@Path("whose") String mediaType,
                                              @Query("api_key") String apiKey);
}
