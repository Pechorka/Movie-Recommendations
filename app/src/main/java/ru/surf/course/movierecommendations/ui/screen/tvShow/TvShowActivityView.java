package ru.surf.course.movierecommendations.ui.screen.tvShow;

import android.content.Context;
import android.content.Intent;
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
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.base.widgets.FavoriteButton;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivityView;
import ru.surf.course.movierecommendations.ui.screen.tvShow.adapters.TVShowInfosPagerAdapter;
import ru.surf.course.movierecommendations.util.Utilities;


public class TvShowActivityView extends BaseActivityView {

    final static String KEY_TV_SHOW = "tv_show";
    final static String KEY_TV_SHOW_ID = "tv_show_id";

    @Inject
    TvShowActivityPresenter presenter;

    private TextView title;
    private TextView releaseDate;
    private ImageView poster;
    private AppBarLayout appBarLayout;
    private View fakeStatusBar;
    private ViewPager infosPager;
    private TextView originalTitle;
    private Toolbar toolbar;
    private FavoriteButton favoriteButton;
    private View errorPlaceholder;

    public static void start(Context context, TVShowInfo tvShowInfo) {
        Intent intent = new Intent(context, TvShowActivityView.class);
        intent.putExtra(KEY_TV_SHOW, tvShowInfo);
        context.startActivity(intent);
    }

    public static void start(Context context, int tv_showId) {
        Intent intent = new Intent(context, TvShowActivityView.class);
        intent.putExtra(KEY_TV_SHOW_ID, tv_showId);
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
        return R.layout.activity_tv_show;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerTvShowActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);

        if (!getIntent().hasExtra(KEY_TV_SHOW) && !getIntent().hasExtra(KEY_TV_SHOW_ID)) {
            onDestroy();
        }

        init();
        initViews();
        setupViews();
    }

    private void init() {

    }

    private void initViews() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.tv_show_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
        }
        title = (TextView) findViewById(R.id.tv_show_title);
        releaseDate = (TextView) findViewById(R.id.tv_show_release_date);
        poster = (ImageView) findViewById(R.id.tv_show_backdrop);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
                R.id.tv_show_collapsing_toolbar_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.tv_show_appbar_layout);
        fakeStatusBar = findViewById(R.id.tv_show_fake_status_bar);
        infosPager = (ViewPager) findViewById(R.id.tv_show_info_infos_pager);
        originalTitle = (TextView) findViewById(R.id.tv_show_original_title);
        toolbar = (Toolbar) findViewById(R.id.tv_show_toolbar);
        favoriteButton = (FavoriteButton) findViewById(R.id.tv_show_favorite_button);
        errorPlaceholder = findViewById(R.id.tv_show_no_internet_screen);
    }

    private void setupViews() {
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            verticalOffset = Math.abs(verticalOffset);
            int offsetToToolbar =
                    appBarLayout1.getTotalScrollRange() - Utilities.getActionBarHeight(TvShowActivityView.this)
                            - fakeStatusBar.getMeasuredHeight();
            if (verticalOffset >= offsetToToolbar) {
                fakeStatusBar.setAlpha((float) (verticalOffset - offsetToToolbar) / Utilities
                        .getActionBarHeight(TvShowActivityView.this));
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

        findViewById(R.id.message_no_internet_button)
                .setOnClickListener(view -> presenter.onRetryBtnClick());
    }


    void fillInformation(TVShowInfo data, boolean isFavorite) {
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
                ArrayList<String> image = new ArrayList<>();
                image.add(data.getPosterPath());
                GalleryActivityView.start(TvShowActivityView.this, image);
            });
        }

        TVShowInfosPagerAdapter tv_showInfosPagerAdapter = new TVShowInfosPagerAdapter(
                getSupportFragmentManager(), this, data);
        infosPager.setAdapter(tv_showInfosPagerAdapter);

        favoriteButton.setInitialState(isFavorite);
    }

    public void onGenreClick(Genre genre) {
        MainActivityView
                .start(this, MainActivityView.class, String.valueOf(genre.getGenreId()), genre.getName(),
                        Media.MediaType.tv);
    }

    void showErrorPlaceholder() {
        errorPlaceholder.setVisibility(View.VISIBLE);
    }

    void hideErrorPlaceholder() {
        errorPlaceholder.setVisibility(View.GONE);
    }

    void hideProgressBar() {
        View progressBarPlaceholder = null;
        progressBarPlaceholder = findViewById(R.id.tv_show_progress_bar_placeholder);
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

    void showFakeStatusBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            fakeStatusBar.setVisibility(View.VISIBLE);
        }
    }

}
