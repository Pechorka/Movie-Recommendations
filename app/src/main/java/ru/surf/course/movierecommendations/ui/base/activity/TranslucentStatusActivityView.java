package ru.surf.course.movierecommendations.ui.base.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public abstract class TranslucentStatusActivityView extends BaseActivityView {

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean viewRecreated) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
