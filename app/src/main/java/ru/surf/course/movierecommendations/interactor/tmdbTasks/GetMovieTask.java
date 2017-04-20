package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo.RetrofitResult;
import rx.Observable;

/**
 * Created by sergey on 19.04.17.
 */

public interface GetMovieTask {

  @GET("{mediaType}/{filter}")
  Observable<RetrofitResult> getMediaByFilter(
      @Path("mediaType") String mediaType,
      @Path("filter") String filter,
      @Query("api_key") String apiKey, @Query("language") String language,
      @Query("page") String page, @Query("region") String region);

  @GET("movie/{movieId}")
  Observable<MovieInfo> getMovieById(@Path("movieId") int id,
      @Query("api_key") String apiKey, @Query("language") String language);

  @GET("search/{mediaType}")
  Observable<RetrofitResult> getMediaByName(@Path("mediaType") String mediaType,
      @Query("query") String name,
      @Query("api_key") String apiKey, @Query("language") String language,
      @Query("page") String page);

  @GET("discover/{mediaType}")
  Observable<RetrofitResult> getMediaByCustomFilter(@Path("mediaType") String mediaType,
      @Query("api_key") String apiKey, @Query("language") String language,
      @Query("page") String page, @Query("region") String region,
      @Query("with_genres") String genres, @Query("release_date.gte") String releaseDateGTE,
      @Query("release_date.gte") String releaseDateLTE, @Query("sort_by") String sort);

  @GET("discover/{mediaType}")
  Observable<RetrofitResult> getMediaByGenreIds(@Path("mediaType") String mediaType,
      @Query("api_key") String apiKey, @Query("language") String language,
      @Query("page") String page, @Query("region") String region,
      @Query("with_genres") String genres);

  @GET("{mediaType}/{mediaId}/similar")
  Observable<RetrofitResult> getSimilarMedia(@Path("mediaType") String mediaType,
      @Path("mediaId") int id,
      @Query("api_key") String apiKey, @Query("language") String language,
      @Query("page") String page);

  @GET("discover/{mediaType}")
  Observable<RetrofitResult> getMediaByKeywords(@Path("mediaType") String mediaType,
      @Query("api_key") String apiKey, @Query("language") String language,
      @Query("page") String page, @Query("region") String region,
      @Query("with_keywords") String keywords);

}
