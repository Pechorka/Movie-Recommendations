package ru.surf.course.movierecommendations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;

/**
 * Created by andrew on 11/26/16.
 */

public class MovieInfoActivity extends AppCompatActivity implements GetMoviesTask.TaskCompletedListener{

    TextView name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Toolbar toolbar = (Toolbar)findViewById(R.id.movie_info_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        ImageView poster = (ImageView)findViewById(R.id.movie_info_poster);
        name = (TextView)findViewById(R.id.movie_info_name);


        int imageID = intent.getIntExtra(MainActivity.IMAGE_ID,0);
        String movieName = intent.getStringExtra(MainActivity.MOVIE_NAME);

        poster.setImageResource(imageID);
        name.setText(movieName);

        int movieId = 346672;

        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(this);
        getMoviesTask.getMovieInfo(movieId, "en");


    }

    @Override
    public void taskCompleted(List<MovieInfo> result) {
        name.setText(result.get(0).name);
    }
}
