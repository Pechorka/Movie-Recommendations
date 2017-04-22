package ru.surf.course.movierecommendations.ui.screen.movie;

import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agna.ferro.mvp.component.ScreenComponent;

import java.text.DateFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.base.widgets.FavoriteButton;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivityView;
import ru.surf.course.movierecommendations.ui.screen.movie.adapters.MovieInfosPagerAdapter;
import ru.surf.course.movierecommendations.util.Utilities;


public class MovieActivityView extends BaseActivityView {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "movie_id";

    @Inject
    MovieActivityPresenter presenter;

    private ProgressBar progressBar;
    private TextView title;
    private TextView releaseDate;
    private ImageView poster;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private View fakeStatusBar;
    private ViewPager infosPager;
    private TextView originalTitle;
    private Toolbar toolbar;
    private FavoriteButton favoriteButton;
    private View errorPlaceholder;


    public static void start(Context context, MovieInfo movieInfo) {
        Intent intent = new Intent(context, MovieActivityView.class);
        intent.putExtra(KEY_MOVIE, movieInfo);
        context.startActivity(intent);
    }

    public static void start(Context context, int movieId) {
        Intent intent = new Intent(context, MovieActivityView.class);
        intent.putExtra(KEY_MOVIE_ID, movieId);
        context.startActivity(intent);
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_movie;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerMovieActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);

        if (!getIntent().hasExtra(KEY_MOVIE) && !getIntent().hasExtra(KEY_MOVIE_ID)) {
            onDestroy();
        }


        init();
        initViews();
        setupViews();
    }

    private void init() {

    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.movie_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
        }
        title = (TextView) findViewById(R.id.movie_title);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        poster = (ImageView) findViewById(R.id.movie_backdrop);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
                R.id.movie_collapsing_toolbar_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.movie_appbar_layout);
        fakeStatusBar = findViewById(R.id.movie_fake_status_bar);
        infosPager = (ViewPager) findViewById(R.id.movie_info_infos_pager);
        originalTitle = (TextView) findViewById(R.id.movie_original_title);
        toolbar = (Toolbar) findViewById(R.id.movie_toolbar);
        favoriteButton = (FavoriteButton) findViewById(R.id.movie_favorite_button);
        errorPlaceholder = findViewById(R.id.movie_no_internet_screen);
    }

    private void setupViews() {
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            verticalOffset = Math.abs(verticalOffset);
            int offsetToToolbar =
                    appBarLayout1.getTotalScrollRange() - Utilities.getActionBarHeight(MovieActivityView.this)
                            - fakeStatusBar.getMeasuredHeight();
            if (verticalOffset >= offsetToToolbar) {
                fakeStatusBar.setAlpha((float) (verticalOffset - offsetToToolbar) / Utilities
                        .getActionBarHeight(MovieActivityView.this));
            }
        });

        favoriteButton.setListener(new FavoriteButton.FavoriteButtonListener() {
            @Override
            public boolean addedToFavorite() {
                try {
                    presenter.addToFavorite();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public boolean removedFromFavorite() {
                try {
                    presenter.removeFromFavorites();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });
        
        findViewById(R.id.message_no_internet_button).setOnClickListener(v -> presenter.onRetryBtnClick());
    }




    


    void fillInformation(MovieInfo data, boolean isFavorite) {
        title.setText(data.getTitle());

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);

        if (data.getDate() != null) {
            releaseDate.setText("(" + dateFormat.format(data.getDate()) + ")");
        }

        originalTitle.setText(data.getOriginalTitle());

        if (Utilities.checkString(data.getPosterPath())) {
            ImageLoader
                    .putPosterNoResize(this, data.getPosterPath(), poster, ImageLoader.sizes.w500);
            poster.setOnClickListener(view -> {
                ArrayList<String> image = new ArrayList<String>();
                image.add(data.getPosterPath());
                GalleryActivityView.start(MovieActivityView.this, image);
            });
        }

        MovieInfosPagerAdapter movieInfosPagerAdapter = new MovieInfosPagerAdapter(
                getSupportFragmentManager(), this, data);
        infosPager.setAdapter(movieInfosPagerAdapter);

        favoriteButton.setInitialState(isFavorite);
    }

    

    

    public void onGenreClick(Genre genre) {
        MainActivityView
                .start(this, MainActivityView.class, String.valueOf(genre.getGenreId()), genre.getName(), true);
    }
    
    void showFakeStatusBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            fakeStatusBar.setVisibility(View.VISIBLE);
        }
    }
    
    void hideProgressBar() {
        View progressBarPlaceholder = null;
        progressBarPlaceholder = findViewById(R.id.movie_progress_bar_placeholder);
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

    void showErrorPlaceholder() {
        errorPlaceholder.setVisibility(View.VISIBLE);
    }

    void hideErrorPlaceholder() {
        errorPlaceholder.setVisibility(View.GONE);
    }

}
