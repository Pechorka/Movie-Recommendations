package ru.surf.course.movierecommendations.app.dagger;

import android.content.Context;

import com.agna.ferro.mvp.component.scope.PerApplication;

import dagger.Component;

@PerApplication
@Component(modules = {
        AppModule.class
})
public interface AppComponent {
    Context context();
}
