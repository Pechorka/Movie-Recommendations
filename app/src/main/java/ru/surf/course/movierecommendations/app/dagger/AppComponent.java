package ru.surf.course.movierecommendations.app.dagger;

import android.content.Context;

import com.agna.ferro.mvp.component.scope.PerApplication;

import dagger.Component;
import ru.surf.course.movierecommendations.interactor.common.network.NetworkModule;
import ru.surf.course.movierecommendations.interactor.common.network.OkHttpModule;
import ru.surf.course.movierecommendations.interactor.common.network.cache.CacheModule;
import ru.surf.course.movierecommendations.ui.base.activity.ActivityModule;

@PerApplication
@Component(modules = {
        AppModule.class,
        OkHttpModule.class,
        NetworkModule.class,
        CacheModule.class,
        ActivityModule.class
})
public interface AppComponent {
    Context context();
}
