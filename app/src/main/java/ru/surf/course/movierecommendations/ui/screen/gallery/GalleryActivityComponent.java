package ru.surf.course.movierecommendations.ui.screen.gallery;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.component.scope.PerScreen;

import dagger.Component;
import ru.surf.course.movierecommendations.app.dagger.AppComponent;
import ru.surf.course.movierecommendations.ui.common.dagger.ActivityViewModule;

@PerScreen
@Component(dependencies = AppComponent.class, modules = {ActivityViewModule.class})
public interface GalleryActivityComponent extends ScreenComponent<GalleryActivityView> {
}
