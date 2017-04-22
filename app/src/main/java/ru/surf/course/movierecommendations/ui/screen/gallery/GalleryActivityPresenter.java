package ru.surf.course.movierecommendations.ui.screen.gallery;

import com.agna.ferro.mvp.component.scope.PerScreen;

import java.util.ArrayList;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;

import static ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView.IMAGES_TAG;
import static ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView.INIT_POSITION_TAG;

@PerScreen
public class GalleryActivityPresenter extends BasePresenter<GalleryActivityView> {

    @Inject
    public GalleryActivityPresenter(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    public void onLoad(boolean viewRecreated) {
        super.onLoad(viewRecreated);

        if (getView().getIntent().hasExtra(IMAGES_TAG)) {
            ArrayList<String> paths = (ArrayList<String>) getView().getIntent().getSerializableExtra(IMAGES_TAG);
            getView().initImagesList(paths);
            if (getView().getIntent().hasExtra(INIT_POSITION_TAG)) {
                getView().setListPosition(getView().getIntent().getIntExtra(INIT_POSITION_TAG, 0));
            }
        }
    }
}
