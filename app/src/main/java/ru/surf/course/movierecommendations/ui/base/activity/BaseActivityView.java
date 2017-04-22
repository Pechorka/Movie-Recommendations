package ru.surf.course.movierecommendations.ui.base.activity;

import android.os.Bundle;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.view.activity.MvpActivityView;

import ru.surf.course.movierecommendations.app.App;
import ru.surf.course.movierecommendations.app.dagger.AppComponent;


public abstract class BaseActivityView extends MvpActivityView {


    public abstract BasePresenter getPresenter();

    protected AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(getPersistentScreenScope());
    }

    public ScreenComponent getScreenComponent() {
        return getPersistentScreenScope().getObject(ScreenComponent.class);
    }


}
