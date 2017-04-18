package ru.surf.course.movierecommendations.tmdbTasks;

import android.content.Context;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import ru.surf.course.movierecommendations.R;

/**
 * Created by andrew on 12/4/16.
 */

public class ImageLoader {

  private static final String TMDB_BASE_POSTER_URL = "https://image.tmdb.org/t/p/";

  public static void putPoster(Context context, String path, ImageView target, sizes size) {
    Picasso.with(context)
        .load(TMDB_BASE_POSTER_URL + size.toString() + "/" + path)
        .placeholder(R.drawable.placeholder_large_dark)
        .noFade()
        .resize(target.getLayoutParams().width, target.getLayoutParams().height)
        .centerCrop()
        .into(target);
  }

  public static void putPosterNoResize(Context context, String path, ImageView target, sizes size) {
    Picasso.with(context)
        .load(TMDB_BASE_POSTER_URL + size.toString() + "/" + path)
        .noFade()
        .into(target);
  }

  public static void getPoster(Context context, String path, int width, int height, Target target,
      sizes size) {
    Picasso.with(context)
        .load(TMDB_BASE_POSTER_URL + size.toString() + "/" + path)
        .noFade()
        .resize(width, height)
        .centerCrop()
        .into(target);
  }

  public static void getPosterNoResize(Context context, String path, Target target, sizes size) {
    Picasso.with(context)
        .load(TMDB_BASE_POSTER_URL + size.toString() + "/" + path)
        .noFade()
        .into(target);
  }

  public static void putPosterNoResize(Context context, String path, ImageView target, sizes size,
      Callback callback) {
    Picasso.with(context)
        .load(TMDB_BASE_POSTER_URL + size.toString() + "/" + path)
        .noFade()
        .into(target, callback);
  }


  public enum sizes {w45, w92, w154, w185, w300, w342, w500, w780}


}
