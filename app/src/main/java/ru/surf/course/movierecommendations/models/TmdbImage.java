package ru.surf.course.movierecommendations.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by andrew on 12/30/16.
 */

public class TmdbImage implements Serializable{

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

    public TmdbImage(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }
}
