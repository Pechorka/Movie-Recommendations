package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.domain.Review;
import rx.Observable;

/**
 * Created by sergey on 20.04.17.
 */

public interface GetReviewsTaskRetrofit {

  @GET("movie/{id}/reviews")
  Observable<Review.RetrofitResult> getMovieReviews(@Path("id") int id,
      @Query("api_key") String apiKey);
}
