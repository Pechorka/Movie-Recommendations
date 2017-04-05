package ru.surf.course.movierecommendations.activities;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import ru.surf.course.movierecommendations.DBHelper;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.adapters.TVShowInfosPagerAdapter;
import ru.surf.course.movierecommendations.custom_views.FavoriteButton;
import ru.surf.course.movierecommendations.models.Favorite;
import ru.surf.course.movierecommendations.models.Genre;
import ru.surf.course.movierecommendations.models.MediaType;
import ru.surf.course.movierecommendations.models.TVShowInfo;
import ru.surf.course.movierecommendations.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetTVShowsTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;


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
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        verticalOffset = Math.abs(verticalOffset);
        int offsetToToolbar =
            appBarLayout.getTotalScrollRange() - Utilities.getActionBarHeight(TvShowActivity.this)
                - fakeStatusBar.getMeasuredHeight();
        if (verticalOffset >= offsetToToolbar) {
          fakeStatusBar.setAlpha((float) (verticalOffset - offsetToToolbar) / Utilities
              .getActionBarHeight(TvShowActivity.this));
        }
      }
    });

    favoriteButton.setListener(new FavoriteButton.FavoriteButtonListener() {
      @Override
      public boolean addedToFavorite() {
        try {
          Favorite favorite = new Favorite();
          favorite.setMediaId(currentMovie.getId());
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
        return false;
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
      ((Button) findViewById(R.id.message_no_internet_button))
          .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (Utilities.isConnectionAvailable(TvShowActivity.this)) {
                errorPlaceholder.setVisibility(View.GONE);
                startLoading();
              }
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
      loadInformationInto(currentMovie, getCurrentLocale().getLanguage());
    }

    if (Build.VERSION.SDK_INT >= 19) {
      fakeStatusBar.setVisibility(View.VISIBLE);
    }
  }

  private void loadInformationInto(final TVShowInfo tvShow, String language) {
    GetTVShowsTask getTvShowsTask = new GetTVShowsTask();
    getTvShowsTask.addListener(new GetMediaTask.TaskCompletedListener() {
      @Override
      public void mediaLoaded(List result, boolean newResult) {
        if (tvShow != null) {
          tvShow.fillFields(result.get(0));
        }
        dataLoadComplete();
      }
    });
    getTvShowsTask.getMediaById(tvShow.getId(), language);
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
      poster.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          ArrayList<String> image = new ArrayList<String>();
          image.add(currentMovie.getPosterPath());
          GalleryActivity.start(TvShowActivity.this, image);
        }
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
    Favorite favorite = dbHelper.getFavoriteByMediaId(currentMovie.getId());
    return favorite != null;
  }

  public void onGenreClick(Genre genre) {
    MainActivity
        .start(this, MainActivity.class, String.valueOf(genre.getId()), genre.getName(), true);
  }

  private Locale getCurrentLocale() {
    return Locale.getDefault();
  }

}
