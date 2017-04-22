package ru.surf.course.movierecommendations.ui.base.fragment;

import android.os.Handler;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.view.fragment.MvpFragmentV4View;

import ru.surf.course.movierecommendations.app.App;
import ru.surf.course.movierecommendations.app.dagger.AppComponent;


/**
 * базовый класс для View, основанной на Fragment
 */
public abstract class BaseFragmentView extends MvpFragmentV4View {

    private static final int DEFAULT_DELAY = 50; //ms
    private Handler handler = new Handler();

    /**
     * Выполняет действие в главном потоке после истечения указанной задержки
     */
    private void runDelayed(Runnable runnable, int delayMs) {
        handler.postDelayed(runnable, delayMs);
    }

    /**
     * То же, что и {@link #runDelayed(Runnable, int)}, только с задержкой по умолчанию
     */
    public void runDelayed(Runnable runnable) {
        runDelayed(runnable, DEFAULT_DELAY);
    }

    /**
     * @return компонент экрана
     */
    public ScreenComponent getScreenComponent() {
        return getPersistentScreenScope().getObject(ScreenComponent.class);
    }

    protected FragmentModule getFragmentModule() {
        return new FragmentModule(getPersistentScreenScope());
    }

    protected AppComponent getAppComponent() {
        return ((App) getActivity().getApplication()).getAppComponent();
    }


}
