package ru.surf.course.movierecommendations.ui.screen.movie;



import com.agna.ferro.mvp.component.scope.PerScreen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.Favorite;
import ru.surf.course.movierecommendations.interactor.network.connection.NetworkConnectionChecker;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMovieTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;
import static ru.surf.course.movierecommendations.ui.screen.movie.MovieActivityView.KEY_MOVIE;
import static ru.surf.course.movierecommendations.ui.screen.movie.MovieActivityView.KEY_MOVIE_ID;

@PerScreen
public class MovieActivityPresenter extends BasePresenter<MovieActivityView> {

    final static int DATA_TO_LOAD = 1;

    private DBHelper dbHelper;
    private Retrofit retrofit;
    private NetworkConnectionChecker connectionChecker;
    private int dataLoaded = 0;
    private MovieInfo currentMovie;

    @Inject
    public MovieActivityPresenter(ErrorHandler errorHandler,
                                  DBHelper dbHelper,
                                  NetworkConnectionChecker networkConnectionChecker,
                                  Retrofit retrofit) {
        super(errorHandler);
        this.dbHelper = dbHelper;
        this.connectionChecker = networkConnectionChecker;
        this.retrofit = retrofit;
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();
        if (connectionChecker.hasInternetConnection())
            startLoading();
        else getView().showErrorPlaceholder();
    }

    void onRetryBtnClick() {
        if (connectionChecker.hasInternetConnection()) {
            getView().hideErrorPlaceholder();
            startLoading();
        }
    }

    private void startLoading() {
        dataLoaded = 0;
        if (getView().getIntent().hasExtra(KEY_MOVIE)) {
            currentMovie = (MovieInfo) getView().getIntent().getSerializableExtra(KEY_MOVIE);
        } else if (getView().getIntent().hasExtra(KEY_MOVIE_ID)) {
            currentMovie = new MovieInfo(getView().getIntent().getIntExtra(KEY_MOVIE_ID, -1));
        }
        if (currentMovie != null) {
            loadInformationInto(currentMovie, Utilities.getSystemLanguage());
        }

        getView().showFakeStatusBar();
    }

    private void loadInformationInto(final MovieInfo movie, String language) {
        GetMovieTask getMovieTask = retrofit.create(GetMovieTask.class);
        Observable<MovieInfo> call = getMovieTask.getMovieById(movie.getMediaId(),
                BuildConfig.TMDB_API_KEY, language);
        subscribeNetworkQuery(call, movieInfo -> {
            movie.fillFields(movieInfo);
            dataLoadComplete();
        });
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Logger.d("data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            getView().fillInformation(currentMovie, isInFavorites());
            getView().hideProgressBar();
        }
    }

    private boolean checkInformation(MovieInfo movie) {
        return Utilities.checkString(movie.getOverview());
        //for now checking only biography
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private boolean isInFavorites() {
        Favorite favorite = dbHelper.getFavoriteByMediaId(currentMovie.getMediaId());
        return favorite != null;
    }

    void addToFavorite() {
        Favorite favorite = new Favorite();
        favorite.setMediaId(currentMovie.getMediaId());
        favorite.setMediaType(Media.MediaType.movie);
        favorite.setTitle(currentMovie.getTitle());
        favorite.setPosterPath(currentMovie.getPosterPath());
        dbHelper.addFavorite(favorite);
    }

    void removeFromFavorites() {
        List<Favorite> favorites = dbHelper.getAllFavorites();
        Favorite favoriteToDelete = null;
        for (Favorite favorite : favorites)
            if (favorite.getMediaId() == currentMovie.getMediaId())
                favoriteToDelete = favorite;
        dbHelper.deleteFavorite(favoriteToDelete);
    }
}
