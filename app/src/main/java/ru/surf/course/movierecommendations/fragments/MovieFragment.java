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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.activities.GalleryActivity;
import ru.surf.course.movierecommendations.activities.MainActivity;
import ru.surf.course.movierecommendations.adapters.MovieInfosPagerAdapter;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 2/11/17.
 */

public class MovieFragment extends Fragment {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "movie_id";
    final static int DATA_TO_LOAD = 1;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private TextView title;
    private TextView releaseDate;
    private MovieInfo currentMovie;
    private ImageView poster;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private View fakeStatusBar;
    private ViewPager infosPager;
    private TextView originalTitle;

    private int dataLoaded = 0;

    public static MovieFragment newInstance(MovieInfo movie) {
        MovieFragment movieInfoFragment = new MovieFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);
        movieInfoFragment.setArguments(bundle);
        return movieInfoFragment;
    }

    public static MovieFragment newInstance(int movieId) {
        MovieFragment movieInfoFragment = new MovieFragment();
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
        setActivityToolbarVisibility(false);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_movie, container, false);
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
        title = (TextView) root.findViewById(R.id.movie_title);
        releaseDate = (TextView) root.findViewById(R.id.movie_release_date);
        poster = (ImageView) root.findViewById(R.id.movie_backdrop);
        collapsingToolbarLayout = (CollapsingToolbarLayout) root.findViewById(R.id.movie_collapsing_toolbar_layout);
        toolbar = (Toolbar) root.findViewById(R.id.movie_toolbar);
        appBarLayout = (AppBarLayout) root.findViewById(R.id.movie_appbar_layout);
        fakeStatusBar = root.findViewById(R.id.movie_fake_status_bar);
        infosPager = (ViewPager) root.findViewById(R.id.movie_info_infos_pager);
        originalTitle = (TextView)root.findViewById(R.id.movie_original_title);
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
        getMoviesTask.addListener(new GetMediaTask.TaskCompletedListener() {
            @Override
            public void mediaLoaded(List<? extends Media> result, boolean newResult) {
                if (movie != null)
                    movie.fillFields(result.get(0));
                dataLoadComplete();
            }
        });
        getMoviesTask.getMediaById(movie.getId(), language);
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
            releaseDate.setText("(" + dateFormat.format(currentMovie.getDate()) + ")");
        }

        originalTitle.setText(currentMovie.getOriginalTitle());

        if (Utilities.checkString(currentMovie.getPosterPath())) {
            ImageLoader.putPosterNoResize(getActivity(), currentMovie.getPosterPath(), poster, ImageLoader.sizes.w500);
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> image = new ArrayList<String>();
                    image.add(currentMovie.getPosterPath());
                    GalleryActivity.start(getActivity(), image);
                }
            });
        }

        MovieInfosPagerAdapter movieInfosPagerAdapter = new MovieInfosPagerAdapter(getChildFragmentManager(), getActivity(), currentMovie);
        infosPager.setAdapter(movieInfosPagerAdapter);
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            fillInformation();
            View progressBarPlaceholder = null;
            if (getView() != null)
                progressBarPlaceholder = getView().findViewById(R.id.movie_progress_bar_placeholder);
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
