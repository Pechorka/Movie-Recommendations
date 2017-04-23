package ru.surf.course.movierecommendations.domain;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class TmdbImage implements Serializable {

    @SerializedName("file_path")
    public String path;
    public Bitmap bitmap;
    @SerializedName("height")
    public int height;
    @SerializedName("width")
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

    public static class RetrofitResultPosters {

        @SerializedName("posters")
        public List<TmdbImage> posters;

        @SerializedName("backdrops")
        public List<TmdbImage> backdrops;
    }

    public static class RetrofitResultProfiles {

        @SerializedName("profiles")
        public List<TmdbImage> profilePictures;
    }
}
