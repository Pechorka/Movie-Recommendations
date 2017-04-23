package ru.surf.course.movierecommendations.ui.screen.customFilter;


import static ru.surf.course.movierecommendations.ui.screen.editPresets.EditPresetsPresenter.KEY_REQUEST_CODE;
import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_MEDIA;

import android.content.Intent;
import com.agna.ferro.mvp.component.scope.PerScreen;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.MovieGenre;
import ru.surf.course.movierecommendations.interactor.TVShowGenre;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMovieGenresTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowGenresTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;


@PerScreen
public class CustomFilterActivityPresenter extends BasePresenter<CustomFilterActivityView> {

  public static final String POPULARITY = "popularity";
  public static final String VOTE_AVERAGE = "vote_average";
  public static final String ASC = "asc";
  public static final String DESC = "desc";
  private final static String BASE_URL = "https://api.themoviedb.org";


  private DBHelper dbHelper;
  private List<? extends Genre> genres = new ArrayList<>();
  private MediaType mediaType;
  private Retrofit retrofit;

  private String genreIds;

  @Inject
  public CustomFilterActivityPresenter(ErrorHandler errorHandler,
      DBHelper dbHelper,
      Retrofit retrofit) {
    super(errorHandler);
    this.dbHelper = dbHelper;
    this.retrofit = retrofit;
  }


  @Override
  public void onLoad(boolean viewRecreated) {
    super.onLoad(viewRecreated);

    if (getView().getIntent().hasExtra(KEY_MEDIA)) {
      mediaType = (MediaType) getView().getIntent().getSerializableExtra(KEY_MEDIA);
    }
    if (getView().getIntent().hasExtra(MediaListFragmentView.KEY_SORT_TYPE)) {
      getView()
          .setupSortRG(getView().getIntent().getStringExtra(MediaListFragmentView.KEY_SORT_TYPE));
    }
    if (getView().getIntent().hasExtra(MediaListFragmentView.KEY_SORT_DIRECTION)) {
      getView().setupSortDirectionRG(
          getView().getIntent().getStringExtra(MediaListFragmentView.KEY_SORT_DIRECTION));
    }
    if (getView().getIntent().hasExtra(MediaListFragmentView.KEY_GENRES)) {
      genreIds = getView().getIntent().getStringExtra(MediaListFragmentView.KEY_GENRES);
    }
    int maxYear = getView().getIntent()
        .getIntExtra(MediaListFragmentView.KEY_MAX_YEAR, Utilities.getCurrentYear());
    int minYear = getView().getIntent().getIntExtra(MediaListFragmentView.KEY_MIN_YEAR, 1930);
    getView().setYearsRangeBarMinValue(minYear);
    getView().setYearsRangeBarMaxValue(maxYear);

    loadGenres();
  }


  public boolean onOptionsItemSelected(int id) {
    switch (id) {
      case android.R.id.home:
        if (getView().getIntent().hasExtra(KEY_REQUEST_CODE)) {
          getView().returnWithResults(new Intent());
        } else {
          getView().onBackPressed();
        }
        return true;
      case R.id.custom_filter_menu_apply:
        Intent intent = new Intent();
        intent.putExtra(MediaListFragmentView.KEY_MIN_YEAR, getView().getYearsRangeBarMinYear());
        intent.putExtra(MediaListFragmentView.KEY_MAX_YEAR, getView().getYearsRangeBarMaxYear());
        intent.putExtra(MediaListFragmentView.KEY_GENRES, getView().getCheckedGenres());
        intent.putExtra(MediaListFragmentView.KEY_SORT_TYPE, getView().getSortType());
        intent.putExtra(MediaListFragmentView.KEY_SORT_DIRECTION, getView().getSortDirection());
        getView().returnWithResults(intent);
        return true;
      case R.id.custom_filter_menu_save_preset:
        if (dbHelper.canAddCustomFilter()) {
          getView().showCustomFilterDialog("save_filter_dialog", mediaType);
        } else {
          getView().showMessage(R.string.to_many_presets);
        }
        return true;
      default:
        return false;
    }
  }

  private void loadGenres() {
    loadGenresFromDB();
    if (genres == null || genres.size() == 0) {
      switch (mediaType) {
        case movie:
          loadMovieGenresFromTMDB();
          break;
        case tv:
          loadTVShowGenresFromTMDB();
          break;
      }
    }
    getView().checkChosenGenres(genreIds);
  }

  private void loadGenresFromDB() {
    switch (mediaType) {
      case movie:
        genres = dbHelper.getAllMovieGenres();
        break;
      case tv:
        genres = dbHelper.getAllTVShowGenres();
        break;
    }
    getView().setGenresListContent(genres);
  }

  private void loadMovieGenresFromTMDB() {
    GetMovieGenresTask getMovieGenresTask = retrofit.create(GetMovieGenresTask.class);
    Observable<MovieGenre.RetrofitResult> call = getMovieGenresTask
        .getGenres(mediaType.toString(), BuildConfig.TMDB_API_KEY);
    subscribeNetworkQuery(call, retrofitResult -> {
      genres = retrofitResult.movieGenres;
      getView().setGenresListContent(genres);
      dbHelper.addMovieGenres(retrofitResult.movieGenres);
    });
  }


  private void loadTVShowGenresFromTMDB() {
    GetTVShowGenresTask getMovieGenresTask = retrofit.create(GetTVShowGenresTask.class);
    Observable<TVShowGenre.RetrofitResult> call = getMovieGenresTask
        .getGenres(mediaType.toString(), BuildConfig.TMDB_API_KEY);
    subscribeNetworkQuery(call, retrofitResult -> {
      genres = retrofitResult.tvShowGenres;
      getView().setGenresListContent(genres);
      dbHelper.addTVShowGenres(retrofitResult.tvShowGenres);
    });
  }
}
