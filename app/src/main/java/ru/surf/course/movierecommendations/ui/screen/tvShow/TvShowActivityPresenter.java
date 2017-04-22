package ru.surf.course.movierecommendations.ui.screen.tvShow;

import com.agna.ferro.mvp.component.scope.PerScreen;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.Favorite;
import ru.surf.course.movierecommendations.interactor.network.connection.NetworkConnectionChecker;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;

import static ru.surf.course.movierecommendations.ui.screen.tvShow.TvShowActivityView.KEY_TV_SHOW;
import static ru.surf.course.movierecommendations.ui.screen.tvShow.TvShowActivityView.KEY_TV_SHOW_ID;

@PerScreen
public class TvShowActivityPresenter extends BasePresenter<TvShowActivityView> {

    private final static int DATA_TO_LOAD = 1;

    private int dataLoaded = 0;

    private NetworkConnectionChecker connectionChecker;
    private Retrofit retrofit;
    private DBHelper dbHelper;

    private TVShowInfo currentMovie;

    @Inject
    public TvShowActivityPresenter(ErrorHandler errorHandler, NetworkConnectionChecker connectionChecker, Retrofit retrofit, DBHelper dbHelper) {
        super(errorHandler);
        this.connectionChecker = connectionChecker;
        this.retrofit = retrofit;
        this.dbHelper = dbHelper;
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        if (connectionChecker.hasInternetConnection()) {
            startLoading();
        } else {
            getView().showErrorPlaceholder();
        }
    }

    private void startLoading() {
        dataLoaded = 0;
        if (getView().getIntent().hasExtra(KEY_TV_SHOW)) {
            currentMovie = (TVShowInfo) getView().getIntent().getSerializableExtra(KEY_TV_SHOW);
        } else if (getView().getIntent().hasExtra(KEY_TV_SHOW_ID)) {
            currentMovie = new TVShowInfo(getView().getIntent().getIntExtra(KEY_TV_SHOW_ID, -1));
        }
        if (currentMovie != null) {
            loadInformationInto(currentMovie, Utilities.getSystemLanguage());
        }

        getView().showFakeStatusBar();
    }

    private void loadInformationInto(final TVShowInfo tvShow, String language) {
        GetTVShowTask getTVShowTask = retrofit.create(GetTVShowTask.class);
        Observable<TVShowInfo> call = getTVShowTask.getTVShowById(tvShow.getMediaId(),
                BuildConfig.TMDB_API_KEY, language);
        subscribeNetworkQuery(call, tvShowInfo -> {
            tvShow.fillFields(tvShowInfo);
            dataLoadComplete();
        });
    }

    private boolean checkInformation(TVShowInfo tvShow) {
        return Utilities.checkString(tvShow.getOverview());
        //for now checking only biography
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Logger.d("data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            getView().fillInformation(currentMovie, isInFavorites());
            getView().hideProgressBar();
        }
    }

    private boolean isInFavorites() {
        Favorite favorite = dbHelper.getFavoriteByMediaId(currentMovie.getMediaId());
        return favorite != null;
    }

    void addToFavorite() {
        Favorite favorite = new Favorite();
        favorite.setMediaId(currentMovie.getMediaId());
        favorite.setMediaType(Media.MediaType.tv);
        favorite.setTitle(currentMovie.getTitle());
        favorite.setPosterPath(currentMovie.getPosterPath());
        dbHelper.addFavorite(favorite);
    }

    void removeFromFavorites() {
        List<Favorite> favorites = dbHelper.getAllFavorites();
        Favorite favoriteToDelete = null;
        for (Favorite favorite : favorites) {
            if (favorite.getMediaId() == currentMovie.getMediaId()) {
                favoriteToDelete = favorite;
            }
        }
        dbHelper.deleteFavorite(favoriteToDelete);
    }

    void onRetryBtnClick() {
        if (connectionChecker.hasInternetConnection()) {
            getView().hideErrorPlaceholder();
            startLoading();
        }
    }
}
