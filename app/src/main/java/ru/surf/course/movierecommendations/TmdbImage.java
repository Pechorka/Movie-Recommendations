package ru.surf.course.movierecommendations;

import android.graphics.Bitmap;

/**
 * Created by andrew on 12/30/16.
 */

public class TmdbImage {

    public String path;
    public Bitmap bitmap;
    public int height;
    public int width;

    public TmdbImage(String path) {
        this.path = path;
    }

    public TmdbImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
    }
}
