package ru.surf.course.movierecommendations.ui.screen.movieReviews;

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

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.presenter.MvpPresenter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Review;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetReviewsTask;
import ru.surf.course.movierecommendations.ui.base.fragment.BaseFragmentView;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.DaggerMovieInfoFragmentComponent;
import ru.surf.course.movierecommendations.ui.screen.movieReviews.adapters.MovieReviewsAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MovieReviewsFragmentView extends BaseFragmentView {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "movie_id";
    
    @Inject
    MovieReviewsFragmentPresenter presenter;

    private ProgressBar progressBar;
    private RecyclerView reviewsList;
    private MovieReviewsAdapter movieReviewsAdapter;
    

    public static MovieReviewsFragmentView newInstance(MovieInfo movie) {
        MovieReviewsFragmentView movieReviewsFragmentView = new MovieReviewsFragmentView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);
        movieReviewsFragmentView.setArguments(bundle);
        return movieReviewsFragmentView;
    }

    public static MovieReviewsFragmentView newInstance(int movieId) {
        MovieReviewsFragmentView movieReviewsFragmentView = new MovieReviewsFragmentView();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        movieReviewsFragmentView.setArguments(bundle);
        return movieReviewsFragmentView;
    }


    @Override
    public MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerMovieReviewsFragmentComponent.builder()
                .fragmentModule(getFragmentModule())
                .appComponent(getAppComponent())
                .build();
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
    

    void fillInformation(MovieInfo data) {
        if (data.getReviews() != null && data.getReviews().size() != 0) {
            movieReviewsAdapter = new MovieReviewsAdapter(getActivity(), data.getReviews());
            reviewsList.setAdapter(movieReviewsAdapter);
        } else {
            getView().findViewById(R.id.fragment_movie_reviews_no_reviews_message_placeholder)
                    .setVisibility(View.VISIBLE);
        }
    }

    void hideProgressBar() {
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
