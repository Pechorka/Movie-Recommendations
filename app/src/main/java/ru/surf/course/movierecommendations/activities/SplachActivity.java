package ru.surf.course.movierecommendations.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import ru.surf.course.movierecommendations.R;

public class SplachActivity extends AppCompatActivity {

    public static final String KEY_RECOMMENDATIONS_SETUP = "rec_setup";
    public static final String KEY_IS_SETUP = "is_setup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);
        delayAndRunActivity(3000, MainActivity.class);
//        if (checkSetup()) {
//            delayAndRunActivity(3000, MainActivity.class);
//        } else {
//            delayAndRunActivity(3000, RecommendationsSetupActivity.class);
//        }
    }

    //delay - в миллисекундах
    private void delayAndRunActivity(long delay, final Class activity) {
        Runnable runnable = new Runnable() {
            public void run() {
                MainActivity.start(SplachActivity.this, activity);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, delay);
    }

    private boolean checkSetup() {
        SharedPreferences prefs = getSharedPreferences(KEY_RECOMMENDATIONS_SETUP, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_SETUP, false);
    }
}
