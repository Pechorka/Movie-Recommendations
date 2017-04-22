package ru.surf.course.movierecommendations.ui.screen.tvShowSeasons;


import com.agna.ferro.mvp.component.scope.PerScreen;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;

import static ru.surf.course.movierecommendations.ui.screen.tvShowSeasons.TvShowSeasonsFragmentView.KEY_TV;
import static ru.surf.course.movierecommendations.ui.screen.tvShowSeasons.TvShowSeasonsFragmentView.KEY_TV_ID;

@PerScreen
public class TvShowSeasonsFragmentPresenter extends BasePresenter<TvShowSeasonsFragmentView> {

    final static int DATA_TO_LOAD = 1;

    private int dataLoaded = 0;

    private TVShowInfo currentTv;

    @Inject
    public TvShowSeasonsFragmentPresenter(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        dataLoaded = 0;
        if (getView().getArguments().containsKey(KEY_TV)) {
            currentTv = (TVShowInfo) getView().getArguments().getSerializable(KEY_TV);
        } else if (getView().getArguments().containsKey(KEY_TV_ID)) {
            currentTv = new TVShowInfo(getView().getArguments().getInt(KEY_TV_ID));
        }
        loadReviews(currentTv);
    }

    private void loadReviews(final TVShowInfo tv) {
        //load seasons
        dataLoadComplete();
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Logger.d("data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            getView().fillInformation(currentTv);
            getView().hideProgressBar();
        }

    }
}
