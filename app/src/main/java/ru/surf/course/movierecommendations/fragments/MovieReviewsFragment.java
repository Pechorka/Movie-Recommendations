package ru.surf.course.movierecommendations.fragments;

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
import java.util.List;
import java.util.Locale;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.MovieReviewsAdapter;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.Review;
import ru.surf.course.movierecommendations.tmdbTasks.GetReviewsTask;

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
    GetReviewsTask getReviewsTask = new GetReviewsTask();
    getReviewsTask.addListener(new GetReviewsTask.ReviewsTaskCompleteListener() {
      @Override
      public void taskCompleted(List<Review> result) {
        movie.setReviews(result);
        dataLoadComplete();
      }
    });
    getReviewsTask.getMovieReviews(currentMovie.getMediaId());
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

  private Locale getCurrentLocale() {
    return Locale.getDefault();
  }

}
