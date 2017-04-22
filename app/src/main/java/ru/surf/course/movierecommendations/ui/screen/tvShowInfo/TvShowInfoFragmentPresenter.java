package ru.surf.course.movierecommendations.ui.screen.tvShowInfo;


import android.util.Log;
import android.view.View;

import com.agna.ferro.mvp.component.scope.PerScreen;

import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static ru.surf.course.movierecommendations.ui.screen.tvShowInfo.TvShowInfoFragmentView.KEY_TV_SHOW;
import static ru.surf.course.movierecommendations.ui.screen.tvShowInfo.TvShowInfoFragmentView.KEY_TV_SHOW_ID;

@PerScreen
public class TvShowInfoFragmentPresenter extends BasePresenter<TvShowInfoFragmentView> {

    final static int DATA_TO_LOAD = 3;

    private int dataLoaded = 0;

    private TVShowInfo currentTvShowInfo;
    private TVShowInfo currentTvShowInfoEnglish;

    private Retrofit retrofit;
    private String apiKey;

    @Inject
    public TvShowInfoFragmentPresenter(ErrorHandler errorHandler, Retrofit retrofit) {
        super(errorHandler);
        this.retrofit = retrofit;
    }

    @Override
    public void onLoad(boolean viewRecreated) {
        super.onLoad(viewRecreated);

        apiKey = BuildConfig.TMDB_API_KEY;
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        dataLoaded = 0;
        if (getView().getArguments().containsKey(KEY_TV_SHOW)) {
            currentTvShowInfo = (TVShowInfo) getView().getArguments().getSerializable(KEY_TV_SHOW);
            dataLoadComplete();
            loadBackdropsInto(currentTvShowInfo);
            loadCreditsInto(currentTvShowInfo);
        } else if (getView().getArguments().containsKey(KEY_TV_SHOW_ID)) {
            currentTvShowInfo = new TVShowInfo(getView().getArguments().getInt(KEY_TV_SHOW_ID));
            loadInformationInto(currentTvShowInfo, Utilities.getSystemLanguage());
        }
    }

    private void loadInformationInto(final TVShowInfo tvShow, String language) {

        GetTVShowTask getTVShowTask = retrofit.create(GetTVShowTask.class);
        Observable<TVShowInfo> call = getTVShowTask
                .getTVShowById(tvShow.getMediaId(), apiKey, language);
        subscribeNetworkQuery(call, tvShowInfo -> {
            tvShow.fillFields(tvShowInfo);
            dataLoadComplete();
            loadBackdropsInto(currentTvShowInfo);
            loadCreditsInto(currentTvShowInfo);
        });
    }

    private void loadBackdropsInto(final TVShowInfo tvShow) {
        GetImagesTask getImagesTask = retrofit.create(GetImagesTask.class);
        Observable<TmdbImage.RetrofitResultPosters> call = getImagesTask
                .getPostersBackdrops(Media.MediaType.movie.toString(), tvShow.getMediaId(), apiKey);
        subscribeNetworkQuery(call, tmdbImages -> {
            tvShow.setBackdrops(tmdbImages.backdrops);
            dataLoadComplete();
        });
    }

    private void loadCreditsInto(final TVShowInfo tvShowInfo) {
        GetCreditsTask getCreditsTask = new GetCreditsTask();
        getCreditsTask.addListener(result -> {
            tvShowInfo.setCredits(result);
            dataLoadComplete();
        });
        getCreditsTask.getTVShowCredits(tvShowInfo.getMediaId());
    }

    private boolean checkInformation(TVShowInfo tvShow) {
        return Utilities.checkString(tvShow.getOverview());
        //for now checking only overview
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentTvShowInfo) && currentTvShowInfoEnglish == null) {
                dataLoaded--;
                currentTvShowInfoEnglish = new TVShowInfo(currentTvShowInfo.getMediaId());
                loadInformationInto(currentTvShowInfoEnglish, Locale.ENGLISH.getLanguage());
            } else {
                getView().fillInformation(currentTvShowInfo, currentTvShowInfoEnglish);
                getView().hideProgressBar();
            }
        }
        Logger.d("data loaded:" + dataLoaded);
    }
}
