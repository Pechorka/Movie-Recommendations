package ru.surf.course.movierecommendations.tmdbTasks;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by andrew on 12/4/16.
 */

public class ImageLoader {

    private static final String TMDB_BASE_POSTER_URL = "https://image.tmdb.org/t/p/w154/";

    public static void putPoster(Context context, String path, ImageView target) {
        Picasso.with(context)
                .load(TMDB_BASE_POSTER_URL + path)
                .centerCrop()
                .into(target);
    }



}
