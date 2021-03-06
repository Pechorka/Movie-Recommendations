package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import rx.Observable;


public interface GetImagesTask {

    @GET("{whose}/{id}/images")
    Observable<TmdbImage.RetrofitResultPosters> getPostersBackdrops(@Path("whose") String mediaType,
                                                                    @Path("id") int id, @Query("api_key") String apiKey);

    @GET("person/{id}/images")
    Observable<TmdbImage.RetrofitResultProfiles> getProfilePictures(@Path("id") int id,
                                                                    @Query("api_key") String apiKey);
}
