package ru.surf.course.movierecommendations.ui.screen.editPresets;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.component.scope.PerScreen;
import dagger.Component;
import ru.surf.course.movierecommendations.app.dagger.AppComponent;
import ru.surf.course.movierecommendations.ui.common.dagger.ActivityViewModule;

/**
 * Created by sergey on 23.04.17.
 */
@PerScreen
@Component(dependencies = AppComponent.class, modules = ActivityViewModule.class)
public interface EditPresetsComponent extends ScreenComponent<EditPresetsView> {

}
