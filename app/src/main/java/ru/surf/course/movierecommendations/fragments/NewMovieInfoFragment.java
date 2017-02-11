package ru.surf.course.movierecommendations.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.MainActivity;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by andrew on 2/11/17.
 */

public class NewMovieInfoFragment extends Fragment {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "movie_id";
    final static int DATA_TO_LOAD = 2;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private TextView title;
    private TextView birthDate;
    private MovieInfo currentMovie;
    private ImageView poster;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private View fakeStatusBar;

    private int dataLoaded = 0;

    public static NewMovieInfoFragment newInstance(MovieInfo movie) {
        NewMovieInfoFragment movieInfoFragment = new NewMovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);
        movieInfoFragment.setArguments(bundle);
        return movieInfoFragment;
    }

    public static NewMovieInfoFragment newInstance(int movieId) {
        NewMovieInfoFragment movieInfoFragment = new NewMovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        movieInfoFragment.setArguments(bundle);
        return movieInfoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).setDrawerEnabled(false);
        setStatusBarTranslucent(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        setActivityToolbarVisibility(false);

        View root = inflater.inflate(R.layout.fragment_new_movie_info, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.movie_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
        title = (TextView) root.findViewById(R.id.new_movie_info_name);
        birthDate = (TextView) root.findViewById(R.id.new_movie_info_birth_date);
        poster = (ImageView) root.findViewById(R.id.new_movie_info_backdrop);
        collapsingToolbarLayout = (CollapsingToolbarLayout) root.findViewById(R.id.new_movie_info_collapsing_toolbar_layout);
        toolbar = (Toolbar) root.findViewById(R.id.new_movie_info_toolbar);
        appBarLayout = (AppBarLayout) root.findViewById(R.id.new_movie_info_appbar_layout);
        fakeStatusBar = root.findViewById(R.id.new_movie_info_fake_status_bar);
    }

    private void setupViews(View root) {
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                verticalOffset = Math.abs(verticalOffset);
                int offsetToToolbar = appBarLayout.getTotalScrollRange() - toolbar.getMeasuredHeight() - fakeStatusBar.getMeasuredHeight();
                if (verticalOffset >= offsetToToolbar) {
                    fakeStatusBar.setAlpha((float)(verticalOffset-offsetToToolbar)/toolbar.getMeasuredHeight());
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dataLoaded = 0;
        if (getArguments().containsKey(KEY_MOVIE)) {
            currentMovie = (MovieInfo) getArguments().getSerializable(KEY_MOVIE);
        } else if (getArguments().containsKey(KEY_MOVIE_ID)) {
            currentMovie = new MovieInfo(getArguments().getInt(KEY_MOVIE_ID));
        }
        if (currentMovie != null) {
            loadInformationInto(currentMovie, getCurrentLocale().getLanguage());
            loadBackdropsInto(currentMovie);
        }

        if (Build.VERSION.SDK_INT >= 19) {
            fakeStatusBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDetach() {
        setActivityToolbarVisibility(true);
        if (getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).setDrawerEnabled(true);
        setStatusBarTranslucent(false);
        fakeStatusBar.setVisibility(View.GONE);
        super.onDetach();
    }

    private void loadInformationInto(final MovieInfo movie, String language) {
        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(new GetMoviesTask.TaskCompletedListener() {
            @Override
            public void moviesLoaded(List<MovieInfo> result, boolean newResult) {
                if (movie != null)
                    movie.fillFields(result.get(0));
                dataLoadComplete();
            }
        });
        getMoviesTask.getMovieById(movie.getId(), language);
    }

    private void loadBackdropsInto(final MovieInfo movie) {
        GetImagesTask getImagesTask = new GetImagesTask();
        getImagesTask.addListener(new GetImagesTask.TaskCompletedListener() {
            @Override
            public void getImagesTaskCompleted(List<TmdbImage> result) {
                movie.setBackdrops(result);
                dataLoadComplete();
            }
        });
        getImagesTask.getMovieImages(movie.getId(), Tasks.GET_BACKDROPS);
    }

    private boolean checkInformation(MovieInfo movie) {
        return Utilities.checkString(movie.getOverview());
        //for now checking only biography
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    private void fillInformation() {
        title.setText(currentMovie.getTitle());

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        if (currentMovie.getDate() != null) {
            birthDate.setText("(" + dateFormat.format(currentMovie.getDate()) + ")");
        }

        if (Utilities.checkString(currentMovie.getPosterPath()))
            ImageLoader.putPosterNoResize(getActivity(), currentMovie.getPosterPath(), poster, ImageLoader.sizes.w500);
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            fillInformation();
            View progressBarPlaceholder = null;
            if (getView() != null)
                progressBarPlaceholder = getView().findViewById(R.id.new_movie_info_progress_bar_placeholder);
            if (progressBarPlaceholder != null)
                progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

    private Locale getCurrentLocale() {
        return Locale.getDefault();
    }

    private void setActivityToolbarVisibility(boolean flag) {
        if (getActivity() instanceof MainActivity) {
            if (flag) {
                ((MainActivity) getActivity()).getSupportActionBar().show();
            } else {
                ((MainActivity) getActivity()).getSupportActionBar().hide();
            }
        }
    }

    private void setStatusBarTranslucent(boolean flag) {
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getActivity().getWindow();
            if (flag)
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }


}
