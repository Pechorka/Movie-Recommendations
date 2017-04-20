package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import ru.surf.course.movierecommendations.domain.Review;
import rx.Observable;

/**
 * Created by sergey on 20.04.17.
 */

public interface GetReviewsTaskRetrofit {

  @GET("person/{id}/images")
  Observable<Review.RetrofitResult> getMovieReviews();
}
