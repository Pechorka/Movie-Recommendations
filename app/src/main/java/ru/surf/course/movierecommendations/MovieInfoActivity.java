package ru.surf.course.movierecommendations;

import android.content.Intent;
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

        Intent intent = getIntent();

        ImageView poster = (ImageView)findViewById(R.id.movie_info_poster);
        TextView name = (TextView)findViewById(R.id.movie_info_name);


        int imageID = intent.getIntExtra(MainActivity.IMAGE_ID,0);
        String movieName = intent.getStringExtra(MainActivity.MOVIE_NAME);

        poster.setImageResource(imageID);
        name.setText(movieName);

    }
}
