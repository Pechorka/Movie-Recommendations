package ru.surf.course.movierecommendations.ui.screen.recommendationsSetup;

import com.agna.ferro.mvp.component.scope.PerScreen;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.RecommendedMovieGenres;
import ru.surf.course.movierecommendations.domain.RecommendedTVShowsGenres;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.network.connection.NetworkConnectionChecker;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetListTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;

@PerScreen
public class RecommendationsSetupActivityPresenter extends
    BasePresenter<RecommendationsSetupActivityView> {

  private final static String BASE_URL = "https://api.themoviedb.org";

  private final static int MOVIE_LIST_ID = 18623;
  private final static int TVSHOW_LIST_ID = 18624;

  private List<Media> mediaList;
  private Retrofit retrofit;
  private NetworkConnectionChecker networkConnectionChecker;
  private DBHelper helper;
  private boolean movie;

  @Inject
  public RecommendationsSetupActivityPresenter(ErrorHandler errorHandler,
      NetworkConnectionChecker networkConnectionChecker
      , Retrofit retrofit, DBHelper helper) {
    super(errorHandler);
    this.networkConnectionChecker = networkConnectionChecker;
    this.retrofit = retrofit;
    this.helper = helper;
  }

  @Override
  public void onLoad(boolean viewRecreated) {
    super.onLoad(viewRecreated);

    if (networkConnectionChecker.hasInternetConnection()) {
      startLoad();
    } else {
      getView().showNoInternetMessage();
    }
    mediaList = new ArrayList<>();
  }

  private void startLoad() {
    GetListTask task = retrofit.create(GetListTask.class);
    Call<RetrofitResult> call = null;
    if (!checkMovieGenresAvailability()) {
      call = task
          .getListById(MOVIE_LIST_ID, BuildConfig.TMDB_API_KEY, Utilities.getSystemLanguage());
      movie = true;
    } else if (!checkTVShowGenresAvailability()) {
      call = task
          .getListById(TVSHOW_LIST_ID, BuildConfig.TMDB_API_KEY, Utilities.getSystemLanguage());
      movie = false;
    } else {
      getView().startMainActivity();
    }
    call.enqueue(new Callback<RetrofitResult>() {
      @Override
      public void onResponse(Call<RetrofitResult> call, Response<RetrofitResult> response) {
        if (response.isSuccessful()) {
          mediaList = response.body().items;
          getView().setMediaListContent(mediaList);
        }
      }

      @Override
      public void onFailure(Call<RetrofitResult> call, Throwable t) {
        t.printStackTrace();
      }
    });

    getView().setProperToolbarTitle(movie);
  }

  void onSaveBtnClick() {
    saveGenresIds();
    if (movie) {
      getView().startRecommendationsActivityWithClearBackstack();
    } else {
      getView().setIsSetup(true);
      getView().startMainActivityWithClearBackstack();
    }
  }

  private void saveGenresIds() {
    Set<Integer> ids = getView().getCheckedGenres();
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

  private boolean checkMovieGenresAvailability() {
    List<RecommendedMovieGenres> recommendedGenres = helper.getAllRecommendedMovieGenres();
    return recommendedGenres != null && recommendedGenres.size() != 0;
  }

  private boolean checkTVShowGenresAvailability() {
    List<RecommendedTVShowsGenres> recommendedGenres = helper.getAllRecommendedTVShowGenres();
    return recommendedGenres != null && recommendedGenres.size() != 0;
  }

  public void onRetryClick() {
    if (networkConnectionChecker.hasInternetConnection()) {
      getView().hideNoInternetMessage();
      startLoad();
    }
  }


  public static class RetrofitResult {

    @SerializedName("items")
    public List<Media> items;

  }
}
