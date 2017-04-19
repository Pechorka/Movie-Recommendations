package ru.surf.course.movierecommendations.ui.base.fragment;

import com.agna.ferro.core.PersistentScreenScope;
import com.agna.ferro.mvp.component.provider.ActivityProvider;
import com.agna.ferro.mvp.component.provider.FragmentProvider;
import com.agna.ferro.mvp.component.scope.PerScreen;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {
    private PersistentScreenScope persistentScreenScope;

    public FragmentModule(PersistentScreenScope persistentScreenScope) {
        this.persistentScreenScope = persistentScreenScope;
    }

    @Provides
    @PerScreen
    public ActivityProvider getActivityProvider() {
        return new ActivityProvider(persistentScreenScope);
    }

    @Provides
    @PerScreen
    public FragmentProvider getFragmentProvider() {
        return new FragmentProvider(persistentScreenScope);
    }
}
