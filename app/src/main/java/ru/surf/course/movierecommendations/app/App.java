package ru.surf.course.movierecommendations.app;


import android.app.Application;

import lombok.Data;
import ru.surf.course.movierecommendations.app.dagger.AppComponent;
import ru.surf.course.movierecommendations.app.dagger.AppModule;
import ru.surf.course.movierecommendations.app.dagger.DaggerAppComponent;

@Data
public class App extends Application{

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initInjector();
    }

    private void initInjector() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
