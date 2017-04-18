package ru.surf.course.movierecommendations.ui.common.dagger;



import dagger.Module;
import ru.surf.course.movierecommendations.ui.base.activity.ActivityModule;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandlerModule;

@Module(includes = {
        ActivityModule.class,
        ErrorHandlerModule.class
})
public class ActivityViewModule {
}
