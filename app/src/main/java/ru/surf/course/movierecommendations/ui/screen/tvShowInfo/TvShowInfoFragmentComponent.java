package ru.surf.course.movierecommendations.ui.screen.tvShowInfo;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.component.scope.PerScreen;

import dagger.Component;
import ru.surf.course.movierecommendations.app.dagger.AppComponent;
import ru.surf.course.movierecommendations.ui.common.dagger.FragmentViewModule;

@PerScreen
@Component(dependencies = AppComponent.class, modules = FragmentViewModule.class)
public interface TvShowInfoFragmentComponent extends ScreenComponent<TvShowInfoFragmentView> {
}
