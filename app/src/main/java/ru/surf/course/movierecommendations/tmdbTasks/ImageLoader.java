package ru.surf.course.movierecommendations.tmdbTasks;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by andrew on 12/4/16.
 */

public class ImageLoader {

    private static final String TMDB_BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static enum sizes {w45, w92, w154, w185, w300, w342, w500, w780}

    public static void putPoster(Context context, String path, ImageView target, sizes size) {
        Picasso.with(context)
                .load(TMDB_BASE_POSTER_URL + size.toString() + "/" + path)
                .noFade()
                .resize(target.getLayoutParams().width, target.getLayoutParams().height)
                .centerCrop()
                .into(target);
    }

    public static void getPoster(Context context, String path, int width, int height, Target target, sizes size) {
        Picasso.with(context)
                .load(TMDB_BASE_POSTER_URL + size.toString() + "/"  + path)
                .noFade()
                .resize(width, height)
                .centerCrop()
                .into(target);
    }



}
