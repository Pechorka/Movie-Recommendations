package ru.surf.course.movierecommendations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by andrew on 11/26/16.
 */

public class MovieInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        Toolbar toolbar = (Toolbar)findViewById(R.id.movie_info_toolbar);
        setSupportActionBar(toolbar);

        ImageView poster = (ImageView)findViewById(R.id.movie_info_poster);
        TextView name = (TextView)findViewById(R.id.movie_info_name);

        //временно
        String[] mNames = {
                "Dr. Strange","Fantastic Beasts"
                ,"Mad Max","Dr. Strange","Fantastic Beasts"
                ,"Mad Max","Dr. Strange","Fantastic Beasts"
                ,"Mad Max","Dr. Strange","Fantastic Beasts"
                ,"Mad Max"
        };
        int[] mThumbIds = {
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max,
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max,
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max,
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max
        };

        Random random = new Random();
        int number = random.nextInt(mThumbIds.length);

        poster.setImageResource(mThumbIds[number]);
        name.setText(mNames[number]);

    }
}
