package ru.surf.course.movierecommendations.app.dagger;

import android.content.Context;

import com.agna.ferro.mvp.component.scope.PerApplication;

import dagger.Component;
import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.SharedPreferencesModule;
import ru.surf.course.movierecommendations.interactor.common.network.NetworkModule;
import ru.surf.course.movierecommendations.interactor.common.network.OkHttpModule;
import ru.surf.course.movierecommendations.interactor.common.network.cache.CacheModule;
import ru.surf.course.movierecommendations.interactor.network.connection.NetworkConnectionChecker;
import ru.surf.course.movierecommendations.ui.base.activity.ActivityModule;
import ru.surf.course.movierecommendations.ui.base.fragment.FragmentModule;

@PerApplication
@Component(modules = {
        AppModule.class,
        OkHttpModule.class,
        NetworkModule.class,
        CacheModule.class,
        FragmentModule.class,
        ActivityModule.class
})
public interface AppComponent {
    Context context();
    NetworkConnectionChecker networkConnectionChecker();
    DBHelper dbHelper();
    Retrofit retrofit();
}
