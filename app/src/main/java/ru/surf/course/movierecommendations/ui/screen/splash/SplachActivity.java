package ru.surf.course.movierecommendations.ui.screen.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivityView;
import ru.surf.course.movierecommendations.ui.screen.recommendationsSetup.RecommendationsSetupActivityView;

public class SplachActivity extends AppCompatActivity {

    public static final String KEY_RECOMMENDATIONS_SETUP = "rec_setup";
    public static final String KEY_IS_SETUP = "is_setup";

    private final static long startDelay = 1000;

    public static void startWithClearBackStack(Context context, Class c) {
        Intent intent = new Intent(context, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);
        if (checkSetup()) {
            delayAndRunActivity(startDelay, MainActivityView.class);
        } else {
            delayAndRunActivity(startDelay, RecommendationsSetupActivityView.class);
        }
    }

    private void delayAndRunActivity(long delay, final Class activity) {
        Runnable runnable = () -> startWithClearBackStack(SplachActivity.this, activity);
        Handler handler = new Handler();
        handler.postDelayed(runnable, delay);
    }

    private boolean checkSetup() {
        SharedPreferences prefs = getSharedPreferences(KEY_RECOMMENDATIONS_SETUP, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_SETUP, false);
    }
}
