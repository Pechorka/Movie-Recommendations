package ru.surf.course.movierecommendations.ui.screen.customFilter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.MovieGenre;
import ru.surf.course.movierecommendations.interactor.TVShowGenre;
import ru.surf.course.movierecommendations.interactor.TVShowGenre.RetrofitResult;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMovieGenresTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowGenresTask;
import ru.surf.course.movierecommendations.ui.screen.customFilter.adapters.GenreListAdapter;
import ru.surf.course.movierecommendations.ui.screen.customFilter.widgets.YearsRangeBar;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivity;
import ru.surf.course.movierecommendations.ui.screen.main.MediaListFragment;
import ru.surf.course.movierecommendations.util.Utilities;

public class CustomFilterActivity extends AppCompatActivity {


  public static final String POPULARITY = "popularity";
  public static final String VOTE_AVERAGE = "vote_average";
  public static final String ASC = "asc";
  public static final String DESC = "desc";
  private final static String BASE_URL = "https://api.themoviedb.org";

  private YearsRangeBar yearsRangeBar;
  private RadioGroup sortRG;
  private RadioGroup sortDirectionRG;
  private RecyclerView genresRV;
  private GenreListAdapter genreListAdapter;
  private LinearLayoutManager linearLayoutManager;

  private List<? extends Genre> genres;
  private MediaType mediaType;
  private Retrofit retrofit;
  private Gson gson;
  private DBHelper dbHelper;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom_filter);
    setupToolbar();
    init();
    loadGenres();
    setupView();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.custom_filter_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      case R.id.custom_filter_menu_save:
        Intent intent = new Intent();
        intent.putExtra(MediaListFragment.KEY_MIN_YEAR, yearsRangeBar.getCurMinYear());
        intent.putExtra(MediaListFragment.KEY_MAX_YEAR, yearsRangeBar.getCurMaxYear());
        intent.putExtra(MediaListFragment.KEY_GENRES, genreListAdapter.getChecked());
        intent.putExtra(MediaListFragment.KEY_SORT_TYPE, getSortType());
        intent.putExtra(MediaListFragment.KEY_SORT_DIRECTION, getSortDirection());
        setResult(RESULT_OK, intent);
        genreListAdapter.saveChecked();
        onBackPressed();
        return true;
      case R.id.custom_filter_menu_save_preset:
        if (dbHelper.canAddCustomFilter()) {
          SaveCustomFilterDialog customFilterDialog = SaveCustomFilterDialog
              .newInstance(genreListAdapter.getChecked(), getSortType(), getSortDirection(),
                  String.valueOf(yearsRangeBar.getCurMinYear()),
                  String.valueOf(yearsRangeBar.getCurMaxYear()), mediaType);
          customFilterDialog.show(getFragmentManager(), "save_filter_dialog");
        } else {
          Toast.makeText(this, R.string.to_many_presets, Toast.LENGTH_LONG).show();
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }


  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.custom_filter_toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_close_white);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.custom);
  }

  private void init() {
    if (getIntent().hasExtra(MainActivity.KEY_MEDIA)) {
      mediaType = (MediaType) getIntent().getSerializableExtra(MainActivity.KEY_MEDIA);
    }
    yearsRangeBar = (YearsRangeBar) findViewById(R.id.custom_filter_years_range_bar);
    sortRG = (RadioGroup) findViewById(R.id.custom_filter_sort_options);
    sortDirectionRG = (RadioGroup) findViewById(R.id.custom_filter_sort_direction_options);
    genresRV = (RecyclerView) findViewById(R.id.custom_filter_genres_rv);
    genres = new ArrayList<>();
    genreListAdapter = new GenreListAdapter(genres, this, mediaType);
    linearLayoutManager = new LinearLayoutManager(this);
    gson = new GsonBuilder()
        .create();
    retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
    dbHelper = DBHelper.getHelper(this);
  }

  private void setupView() {
    genresRV.setAdapter(genreListAdapter);
    genresRV.setLayoutManager(linearLayoutManager);
    if (getIntent().hasExtra(MediaListFragment.KEY_SORT_TYPE)) {
      setupSortRG(getIntent().getStringExtra(MediaListFragment.KEY_SORT_TYPE));
    }
    if (getIntent().hasExtra(MediaListFragment.KEY_SORT_DIRECTION)) {
      setupSortDirectionRG(getIntent().getStringExtra(MediaListFragment.KEY_SORT_DIRECTION));
    }
    int maxYear = getIntent()
        .getIntExtra(MediaListFragment.KEY_MAX_YEAR, Utilities.getCurrentYear());
    int minYear = getIntent().getIntExtra(MediaListFragment.KEY_MIN_YEAR, 1930);
    yearsRangeBar.setStartMaxMinValues(maxYear, minYear);

  }

  private void setupSortRG(String sortType) {
    switch (sortType) {
      case POPULARITY:
        sortRG.check(R.id.sort_by_popularity);
        break;
      case VOTE_AVERAGE:
        sortRG.check(R.id.sort_by_average_votes);
        break;
    }
  }

  private void setupSortDirectionRG(String sortDirection) {
    switch (sortDirection) {
      case ASC:
        sortDirectionRG.check(R.id.ascending_order);
        break;
      case DESC:
        sortDirectionRG.check(R.id.descending_order);
        break;
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
    genreListAdapter.setGenreList(genres);
  }

  private void loadMovieGenresFromTMDB() {
    Callback<MovieGenre.RetrofitResult> callback = new Callback<MovieGenre.RetrofitResult>() {
      @Override
      public void onResponse(Call<MovieGenre.RetrofitResult> call,
          Response<MovieGenre.RetrofitResult> response) {
        if (response.isSuccessful()) {
          genres = response.body().movieGenres;
          genreListAdapter.setGenreList(genres);
          dbHelper.addMovieGenres(response.body().movieGenres);
        }
      }

      @Override
      public void onFailure(Call<MovieGenre.RetrofitResult> call, Throwable t) {
        Log.d("tag", t.getMessage());
      }
    };
    GetMovieGenresTask task = retrofit.create(GetMovieGenresTask.class);
    Call<MovieGenre.RetrofitResult> call = task
        .getGenres(mediaType.toString(), BuildConfig.TMDB_API_KEY);
    call.enqueue(callback);
  }

  private void loadTVShowGenresFromTMDB() {
    Callback<TVShowGenre.RetrofitResult> callback = new Callback<RetrofitResult>() {
      @Override
      public void onResponse(Call<TVShowGenre.RetrofitResult> call,
          Response<TVShowGenre.RetrofitResult> response) {
        if (response.isSuccessful()) {
          genres = response.body().tvShowGenres;
          genreListAdapter.setGenreList(genres);
          dbHelper.addTVShowGenres(response.body().tvShowGenres);
        }
      }

      @Override
      public void onFailure(Call<TVShowGenre.RetrofitResult> call, Throwable t) {

      }
    };
    GetTVShowGenresTask task = retrofit.create(GetTVShowGenresTask.class);
    Call<TVShowGenre.RetrofitResult> call = task
        .getGenres(mediaType.toString(), BuildConfig.TMDB_API_KEY);
    call.enqueue(callback);
  }

  private String getSortType() {
    switch (sortRG.getCheckedRadioButtonId()) {
      case R.id.sort_by_popularity:
        return POPULARITY;
      case R.id.sort_by_average_votes:
        return VOTE_AVERAGE;
      default:
        return POPULARITY;
    }
  }

  private String getSortDirection() {
    switch (sortDirectionRG.getCheckedRadioButtonId()) {
      case R.id.ascending_order:
        return ASC;
      case R.id.descending_order:
        return DESC;
      default:
        return DESC;
    }
  }

}
