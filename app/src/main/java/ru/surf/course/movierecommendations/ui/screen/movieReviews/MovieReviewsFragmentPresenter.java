package ru.surf.course.movierecommendations.ui.screen.movieReviews;


import com.agna.ferro.mvp.component.scope.PerScreen;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.Review;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetReviewsTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import rx.Observable;

import static ru.surf.course.movierecommendations.ui.screen.movieReviews.MovieReviewsFragmentView.KEY_MOVIE;
import static ru.surf.course.movierecommendations.ui.screen.movieReviews.MovieReviewsFragmentView.KEY_MOVIE_ID;

@PerScreen
public class MovieReviewsFragmentPresenter extends BasePresenter<MovieReviewsFragmentView> {

    private final static int DATA_TO_LOAD = 1;

    private Retrofit retrofit;
    private MovieInfo currentMovie;
    private int dataLoaded = 0;

    @Inject
    public MovieReviewsFragmentPresenter(ErrorHandler errorHandler,
                                         Retrofit retrofit) {
        super(errorHandler);
        this.retrofit = retrofit;
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        dataLoaded = 0;
        if (getView().getArguments().containsKey(KEY_MOVIE)) {
            currentMovie = (MovieInfo) getView().getArguments().getSerializable(KEY_MOVIE);
        } else if (getView().getArguments().containsKey(KEY_MOVIE_ID)) {
            currentMovie = new MovieInfo(getView().getArguments().getInt(KEY_MOVIE_ID));
        }
        loadReviews(currentMovie);
    }

    private void loadReviews(final MovieInfo movie) {
        GetReviewsTask getReviewsTask = retrofit.create(GetReviewsTask.class);
        Observable<Review.RetrofitResult> call = getReviewsTask
                .getMovieReviews(movie.getMediaId(),
                        BuildConfig.TMDB_API_KEY);
        subscribeNetworkQuery(call, retrofitResult -> {
            movie.setReviews(retrofitResult.reviews);
            dataLoadComplete();
        });
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Logger.d("data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            getView().fillInformation(currentMovie);
            getView().hideProgressBar();
        }

    }
}
