package ru.surf.course.movierecommendations.ui.screen.movie;

import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Review;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetReviewsTask;
import ru.surf.course.movierecommendations.ui.screen.movie.adapters.MovieReviewsAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by andrew on 2/18/17.
 */

public class MovieReviewsFragment extends Fragment {

  final static String KEY_MOVIE = "movie";
  final static String KEY_MOVIE_ID = "movie_id";
  final static int DATA_TO_LOAD = 1;
  final String LOG_TAG = getClass().getSimpleName();

  private ProgressBar progressBar;
  private MovieInfo currentMovie;
  private RecyclerView reviewsList;
  private MovieReviewsAdapter movieReviewsAdapter;

  private int dataLoaded = 0;

  public static MovieReviewsFragment newInstance(MovieInfo movie) {
    MovieReviewsFragment movieReviewsFragment = new MovieReviewsFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(KEY_MOVIE, movie);
    movieReviewsFragment.setArguments(bundle);
    return movieReviewsFragment;
  }

  public static MovieReviewsFragment newInstance(int movieId) {
    MovieReviewsFragment movieReviewsFragment = new MovieReviewsFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_MOVIE_ID, movieId);
    movieReviewsFragment.setArguments(bundle);
    return movieReviewsFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (getArguments() == null) {
      onDestroy();
    }

    View root = inflater.inflate(R.layout.fragment_movie_reviews, container, false);
    initViews(root);
    setupViews(root);

    return root;
  }

  private void initViews(View root) {
    progressBar = (ProgressBar) root.findViewById(R.id.fragment_movie_reviews_progress_bar);
    if (progressBar != null) {
      progressBar.setIndeterminate(true);
      progressBar.getIndeterminateDrawable()
          .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent),
              PorterDuff.Mode.MULTIPLY);
    }

    reviewsList = (RecyclerView) root.findViewById(R.id.fragment_movie_reviews_list);
  }

  private void setupViews(View root) {
    reviewsList.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    dataLoaded = 0;
    if (getArguments().containsKey(KEY_MOVIE)) {
      currentMovie = (MovieInfo) getArguments().getSerializable(KEY_MOVIE);
    } else if (getArguments().containsKey(KEY_MOVIE_ID)) {
      currentMovie = new MovieInfo(getArguments().getInt(KEY_MOVIE_ID));
    }
    loadReviews(currentMovie);
  }

  private void loadReviews(final MovieInfo movie) {
    RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory
        .createWithScheduler(Schedulers.io());
    Gson gson = new GsonBuilder().create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(rxAdapter)
        .build();
    GetReviewsTask getReviewsTask = retrofit.create(GetReviewsTask.class);
    Observable<Review.RetrofitResult> call = getReviewsTask
        .getMovieReviews(movie.getMediaId(),
            BuildConfig.TMDB_API_KEY);
    call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(retrofitResult -> {
          movie.setReviews(retrofitResult.reviews);
          dataLoadComplete();
        });
  }

  private void fillInformation() {
    if (currentMovie.getReviews() != null && currentMovie.getReviews().size() != 0) {
      movieReviewsAdapter = new MovieReviewsAdapter(getActivity(), currentMovie.getReviews());
      reviewsList.setAdapter(movieReviewsAdapter);
    } else {
      getView().findViewById(R.id.fragment_movie_reviews_no_reviews_message_placeholder)
          .setVisibility(View.VISIBLE);
    }
  }

  private void dataLoadComplete() {
    dataLoaded++;
    Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    if (dataLoaded == DATA_TO_LOAD) {
      fillInformation();
      View progressBarPlaceholder = null;
      if (getView() != null) {
        progressBarPlaceholder = getView()
            .findViewById(R.id.fragment_movie_reviews_progress_bar_placeholder);
      }
      if (progressBarPlaceholder != null) {
        progressBarPlaceholder.setVisibility(View.GONE);
      }

    }

  }

}
