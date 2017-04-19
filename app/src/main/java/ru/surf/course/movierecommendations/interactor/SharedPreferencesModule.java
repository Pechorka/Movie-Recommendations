package ru.surf.course.movierecommendations.interactor;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.agna.ferro.mvp.component.scope.PerScreen;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

    @PerScreen
    @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
