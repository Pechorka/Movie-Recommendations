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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.Favorite;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowsTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.ui.base.widgets.FavoriteButton;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivity;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivity;
import ru.surf.course.movierecommendations.ui.screen.tvShow.adapters.TVShowInfosPagerAdapter;
import ru.surf.course.movierecommendations.util.Utilities;


/**
 * Created by andrew on 2/19/17.
 */

public class TvShowActivity extends AppCompatActivity {

  final static String KEY_TV_SHOW = "tv_show";
  final static String KEY_TV_SHOW_ID = "tv_show_id";
  final static int DATA_TO_LOAD = 1;
  final String LOG_TAG = getClass().getSimpleName();

  private DBHelper dbHelper;

  private ProgressBar progressBar;
  private TextView title;
  private TextView releaseDate;
  private TVShowInfo currentMovie;
  private ImageView poster;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private AppBarLayout appBarLayout;
  private View fakeStatusBar;
  private ViewPager infosPager;
  private TextView originalTitle;
  private Toolbar toolbar;
  private FavoriteButton favoriteButton;

  private int dataLoaded = 0;

  public static void start(Context context, TVShowInfo tvShowInfo) {
    Intent intent = new Intent(context, TvShowActivity.class);
    intent.putExtra(KEY_TV_SHOW, tvShowInfo);
    context.startActivity(intent);
  }

  public static void start(Context context, int tv_showId) {
    Intent intent = new Intent(context, TvShowActivity.class);
    intent.putExtra(KEY_TV_SHOW_ID, tv_showId);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!getIntent().hasExtra(KEY_TV_SHOW) && !getIntent().hasExtra(KEY_TV_SHOW_ID)) {
      onDestroy();
    }

    setContentView(R.layout.activity_tv_show);

    init();
    initViews();
    setupViews();
  }

  private void init() {
    dbHelper = DBHelper.getHelper(this);
  }

  private void initViews() {
    progressBar = (ProgressBar) findViewById(R.id.tv_show_progress_bar);
    if (progressBar != null) {
      progressBar.setIndeterminate(true);
      progressBar.getIndeterminateDrawable()
          .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
              PorterDuff.Mode.MULTIPLY);
    }
    title = (TextView) findViewById(R.id.tv_show_title);
    releaseDate = (TextView) findViewById(R.id.tv_show_release_date);
    poster = (ImageView) findViewById(R.id.tv_show_backdrop);
    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
        R.id.tv_show_collapsing_toolbar_layout);
    appBarLayout = (AppBarLayout) findViewById(R.id.tv_show_appbar_layout);
    fakeStatusBar = findViewById(R.id.tv_show_fake_status_bar);
    infosPager = (ViewPager) findViewById(R.id.tv_show_info_infos_pager);
    originalTitle = (TextView) findViewById(R.id.tv_show_original_title);
    toolbar = (Toolbar) findViewById(R.id.tv_show_toolbar);
    favoriteButton = (FavoriteButton) findViewById(R.id.tv_show_favorite_button);
  }

  private void setupViews() {
    toolbar.setNavigationIcon(R.drawable.ic_action_back);
    toolbar.setNavigationOnClickListener(v -> onBackPressed());

    appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
      verticalOffset = Math.abs(verticalOffset);
      int offsetToToolbar =
          appBarLayout1.getTotalScrollRange() - Utilities.getActionBarHeight(TvShowActivity.this)
              - fakeStatusBar.getMeasuredHeight();
      if (verticalOffset >= offsetToToolbar) {
        fakeStatusBar.setAlpha((float) (verticalOffset - offsetToToolbar) / Utilities
            .getActionBarHeight(TvShowActivity.this));
      }
    });

    favoriteButton.setListener(new FavoriteButton.FavoriteButtonListener() {
      @Override
      public boolean addedToFavorite() {
        try {
          Favorite favorite = new Favorite();
          favorite.setMediaId(currentMovie.getMediaId());
          favorite.setMediaType(MediaType.tv);
          favorite.setTitle(currentMovie.getTitle());
          favorite.setPosterPath(currentMovie.getPosterPath());
          dbHelper.addFavorite(favorite);
          return true;
        } catch (Exception e) {
          return false;
        }
      }

      @Override
      public boolean removedFromFavorite() {
          try {
              List<Favorite> favorites = dbHelper.getAllFavorites();
              Favorite favoriteToDelete = null;
              for (Favorite favorite : favorites)
                  if (favorite.getMediaId() == currentMovie.getMediaId())
                      favoriteToDelete = favorite;
              dbHelper.deleteFavorite(favoriteToDelete);
              return true;
          } catch (Exception e) {
              return false;
          }
      }
    });
  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    if (Utilities.isConnectionAvailable(this)) {
      startLoading();
    } else {
      final View errorPlaceholder = findViewById(R.id.tv_show_no_internet_screen);
      errorPlaceholder.setVisibility(View.VISIBLE);
      findViewById(R.id.message_no_internet_button)
          .setOnClickListener(view -> {
            if (Utilities.isConnectionAvailable(TvShowActivity.this)) {
              errorPlaceholder.setVisibility(View.GONE);
              startLoading();
            }
          });
    }
  }

  private void startLoading() {
    dataLoaded = 0;
    if (getIntent().hasExtra(KEY_TV_SHOW)) {
      currentMovie = (TVShowInfo) getIntent().getSerializableExtra(KEY_TV_SHOW);
    } else if (getIntent().hasExtra(KEY_TV_SHOW_ID)) {
      currentMovie = new TVShowInfo(getIntent().getIntExtra(KEY_TV_SHOW_ID, -1));
    }
    if (currentMovie != null) {
      loadInformationInto(currentMovie, Utilities.getSystemLanguage());
    }

    if (Build.VERSION.SDK_INT >= 19) {
      fakeStatusBar.setVisibility(View.VISIBLE);
    }
  }

  private void loadInformationInto(final TVShowInfo tvShow, String language) {
    GetTVShowsTask getTvShowsTask = new GetTVShowsTask();
    getTvShowsTask.addListener((result, newResult) -> {
      if (tvShow != null) {
        tvShow.fillFields(result.get(0));
      }
      dataLoadComplete();
    });
    getTvShowsTask.getMediaById(tvShow.getMediaId(), language);
  }

  private boolean checkInformation(TVShowInfo tvShow) {
    return Utilities.checkString(tvShow.getOverview());
    //for now checking only biography
  }

  private String firstLetterToUpper(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }


  private void fillInformation() {
    title.setText(currentMovie.getTitle());

    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);

    if (currentMovie.getDate() != null) {
      releaseDate.setText("(" + dateFormat.format(currentMovie.getDate()) + ")");
    }

    originalTitle.setText(currentMovie.getOriginalTitle());

    if (Utilities.checkString(currentMovie.getPosterPath())) {
      ImageLoader
          .putPosterNoResize(this, currentMovie.getPosterPath(), poster, ImageLoader.sizes.w500);
      poster.setOnClickListener(view -> {
        ArrayList<String> image = new ArrayList<String>();
        image.add(currentMovie.getPosterPath());
        GalleryActivity.start(TvShowActivity.this, image);
      });
    }

    TVShowInfosPagerAdapter tv_showInfosPagerAdapter = new TVShowInfosPagerAdapter(
        getSupportFragmentManager(), this, currentMovie);
    infosPager.setAdapter(tv_showInfosPagerAdapter);

    favoriteButton.setInitialState(isInFavorites());
  }

  private void dataLoadComplete() {
    dataLoaded++;
    Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    if (dataLoaded == DATA_TO_LOAD) {
      fillInformation();
      View progressBarPlaceholder = null;
      progressBarPlaceholder = findViewById(R.id.tv_show_progress_bar_placeholder);
      if (progressBarPlaceholder != null) {
        progressBarPlaceholder.setVisibility(View.GONE);
      }
    }
  }

  private boolean isInFavorites() {
    Favorite favorite = dbHelper.getFavoriteByMediaId(currentMovie.getMediaId());
    return favorite != null;
  }

  public void onGenreClick(Genre genre) {
    MainActivity
        .start(this, MainActivity.class, String.valueOf(genre.getGenreId()), genre.getName(), true);
  }

}
