package ru.surf.course.movierecommendations.ui.screen.favorites;


import com.agna.ferro.mvp.component.scope.PerScreen;

import java.util.List;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.Favorite;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;

@PerScreen
public class FavoritesActivityPresenter extends BasePresenter<FavoritesActivityView> {

    private DBHelper dbHelper;

    @Inject
    public FavoritesActivityPresenter(ErrorHandler errorHandler, DBHelper dbHelper) {
        super(errorHandler);
        this.dbHelper = dbHelper;
    }

    @Override
    public void onLoad(boolean viewRecreated) {
        super.onLoad(viewRecreated);

        init();
    }

    private void init() {
        List<Favorite> favoriteList = dbHelper.getAllFavorites();
        if (favoriteList != null && favoriteList.size() != 0) {
            getView().setFavoritesContent(favoriteList);
        } else {
            getView().showEmptyMessage();
        }
    }
}
