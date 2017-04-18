package ru.surf.course.movierecommendations.ui.screen.recommendationsSetup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.RecommendedMovieGenres;
import ru.surf.course.movierecommendations.domain.RecommendedTVShowsGenres;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetListTask;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivity;
import ru.surf.course.movierecommendations.ui.screen.recommendationsSetup.adapters.RecommendationsSetupListAdapter;
import ru.surf.course.movierecommendations.ui.screen.splash.SplachActivity;

public class RecommendationsSetupActivity extends AppCompatActivity implements
    Callback<RecommendationsSetupActivity.RetrofitResult> {

  private final static String BASE_URL = "https://api.themoviedb.org";

  private final static int MOVIE_LIST_ID = 18623;
  private final static int TVSHOW_LIST_ID = 18624;

  private List<Media> mediaList;
  private Gson gson;
  private Retrofit retrofit;
  private DBHelper helper;
  private boolean movie;

  private RecyclerView recommendSetupList;
  private RecommendationsSetupListAdapter adapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recommendations_setup);
    init();
    setup();
    GetListTask task = retrofit.create(GetListTask.class);
    Call<RecommendationsSetupActivity.RetrofitResult> call = null;
    if (!checkMovieGenresAvailability()) {
      call = task.getListById(MOVIE_LIST_ID, BuildConfig.TMDB_API_KEY, "en");
      movie = true;
    } else if (!checkTVShowGenresAvailability()) {
      call = task.getListById(TVSHOW_LIST_ID, BuildConfig.TMDB_API_KEY, "en");
      movie = false;
    } else {
      MainActivity.start(this, MainActivity.class);
    }
    setupToolbar();
    call.enqueue(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.recommended_setup_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.recommendations_setup_menu_save:
        saveGenresIds();
        if (movie) {
          MainActivity.start(this, RecommendationsSetupActivity.class);
        } else {
          setIsSetup(true);
          MainActivity.start(this, MainActivity.class);
        }
        break;
      case R.id.recommendations_setup_menu_skip:
        setIsSetup(true);
        MainActivity.start(this, MainActivity.class);
        break;

    }
    return super.onOptionsItemSelected(item);
  }

  private void saveGenresIds() {
    Set<Integer> ids = adapter.getGenres();
    if (movie) {
      for (int id : ids) {
        helper.addRecommendedMovieGenre(id);
      }
    } else {
      for (int id : ids) {
        helper.addRecommendedTVShowGenre(id);
      }
    }
  }

  @Override
  public void onResponse(Call<RecommendationsSetupActivity.RetrofitResult> call,
      Response<RecommendationsSetupActivity.RetrofitResult> response) {
    if (response.isSuccessful()) {
      mediaList = response.body().items;
      adapter.setMediaList(mediaList);
    }
  }

  @Override
  public void onFailure(Call<RecommendationsSetupActivity.RetrofitResult> call, Throwable t) {
    t.printStackTrace();
  }

  private void init() {
    recommendSetupList = (RecyclerView) findViewById(R.id.recommendations_setup_rv);
    mediaList = new ArrayList<>();
    adapter = new RecommendationsSetupListAdapter(this, mediaList);
    helper = DBHelper.getHelper(this);
    gson = new GsonBuilder()
        .create();
    retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
  }

  private void setup() {
    recommendSetupList.setAdapter(adapter);
    recommendSetupList.setLayoutManager(new GridLayoutManager(this, 2));
  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.activity_recomendation_setup_toolbar);
    setSupportActionBar(toolbar);
    if (movie) {
      getSupportActionBar().setTitle(R.string.movie_setup_title);
    }else {
      getSupportActionBar().setTitle(R.string.tvshow_setup_title);
    }
  }

  private boolean checkMovieGenresAvailability() {
    List<RecommendedMovieGenres> recommendedGenres = helper.getAllRecommendedMovieGenres();
    return recommendedGenres != null && recommendedGenres.size() != 0;
  }

  private boolean checkTVShowGenresAvailability() {
    List<RecommendedTVShowsGenres> recommendedGenres = helper.getAllRecommendedTVShowGenres();
    return recommendedGenres != null && recommendedGenres.size() != 0;
  }

  private void setIsSetup(boolean value){
    SharedPreferences prefs = getSharedPreferences(SplachActivity.KEY_RECOMMENDATIONS_SETUP,
        MODE_PRIVATE);
    prefs.edit().putBoolean(SplachActivity.KEY_IS_SETUP, value).apply();
  }

  public static class RetrofitResult {

    @SerializedName("items")
    public List<Media> items;
  }


}
