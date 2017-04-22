package ru.surf.course.movierecommendations.ui.screen.movieInfo;

import android.util.Log;
import android.view.View;

import com.agna.ferro.mvp.component.scope.PerScreen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMovieTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;
import static ru.surf.course.movierecommendations.ui.screen.movieInfo.MovieInfoFragmentView.KEY_MOVIE;
import static ru.surf.course.movierecommendations.ui.screen.movieInfo.MovieInfoFragmentView.KEY_MOVIE_ID;

@PerScreen
public class MovieInfoFragmentPresenter extends BasePresenter<MovieInfoFragmentView> {

    final static int DATA_TO_LOAD = 3;

    private MovieInfo currentMovieInfo;
    private MovieInfo currentMovieInfoEnglish;

    private Retrofit retrofit;
    private String apiKey;

    private int dataLoaded = 0;

    @Inject
    public MovieInfoFragmentPresenter(ErrorHandler errorHandler,
                                      Retrofit retrofit) {
        super(errorHandler);
        this.retrofit = retrofit;
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        init();
    }

    private void init() {
        apiKey = BuildConfig.TMDB_API_KEY;

        dataLoaded = 0;
        if (getView().getArguments().containsKey(KEY_MOVIE)) {
            currentMovieInfo = (MovieInfo) getView().getArguments().getSerializable(KEY_MOVIE);
            dataLoadComplete();
            loadBackdropsInto(currentMovieInfo);
            loadCreditsInto(currentMovieInfo);
        } else if (getView().getArguments().containsKey(KEY_MOVIE_ID)) {
            currentMovieInfo = new MovieInfo(getView().getArguments().getInt(KEY_MOVIE_ID));
            loadInformationInto(currentMovieInfo, Utilities.getSystemLanguage());
        }
    }

    private void loadInformationInto(final MovieInfo movie, String language) {
        GetMovieTask getMovieTask = retrofit.create(GetMovieTask.class);
        Observable<MovieInfo> call = getMovieTask.getMovieById(movie.getMediaId(), apiKey, language);
        subscribeNetworkQuery(call, movieInfo -> {
            movie.fillFields(movieInfo);
            dataLoadComplete();
            loadBackdropsInto(currentMovieInfo);
            loadCreditsInto(currentMovieInfo);
        });
    }

    private void loadBackdropsInto(final MovieInfo movie) {
        GetImagesTask getImagesTask = retrofit.create(GetImagesTask.class);
        Observable<TmdbImage.RetrofitResultPosters> call = getImagesTask
                .getPostersBackdrops(Media.MediaType.movie.toString(), movie.getMediaId(), apiKey);
        subscribeNetworkQuery(call, tmdbImages -> {
            movie.setBackdrops(tmdbImages.backdrops);
            dataLoadComplete();
        });
//    GetImagesTask getImagesTask = new GetImagesTask();
//    getImagesTask.addListener(result -> {
//      movie.setBackdrops(result);
//      dataLoadComplete();
//    });
//    getImagesTask.getMovieImages(movie.getMediaId(), Tasks.GET_BACKDROPS);
    }

    private void loadCreditsInto(final MovieInfo movieInfo) {
        GetCreditsTask getCreditsTask = new GetCreditsTask();
        getCreditsTask.addListener(result -> {
            movieInfo.setCredits(result);
            dataLoadComplete();
        });
        getCreditsTask.getMovieCredits(movieInfo.getMediaId());
    }

    private boolean checkInformation(MovieInfo movie) {
        return Utilities.checkString(movie.getOverview());
        //for now checking only overview
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentMovieInfo) && currentMovieInfoEnglish == null) {
                dataLoaded--;
                currentMovieInfoEnglish = new MovieInfo(currentMovieInfo.getMediaId());
                loadInformationInto(currentMovieInfoEnglish, Locale.ENGLISH.getLanguage());
            } else {
                getView().fillInformation(currentMovieInfo, currentMovieInfoEnglish);
                getView().hideProgressBar();
            }
        }
        Logger.d("data loaded:" + dataLoaded);
    }
}
