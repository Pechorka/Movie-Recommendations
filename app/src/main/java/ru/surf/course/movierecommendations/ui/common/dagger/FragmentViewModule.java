package ru.surf.course.movierecommendations.ui.common.dagger;

import dagger.Module;
import ru.surf.course.movierecommendations.ui.base.fragment.FragmentModule;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandlerModule;


@Module(includes = {
        FragmentModule.class,
        ErrorHandlerModule.class})
public class FragmentViewModule {
}
