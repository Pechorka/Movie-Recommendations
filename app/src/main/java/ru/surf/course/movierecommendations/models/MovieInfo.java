package ru.surf.course.movierecommendations.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieInfo extends Media implements Serializable{

    private String mRevenue;
    private int mRuntime;

    public MovieInfo(int id) {
        super(id);
    }

    public String getRevenue() {
        return mRevenue;
    }

    public void setRevenue(String revenue) {
        this.mRevenue = revenue;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public void setRuntime(int runtime) {
        this.mRuntime = runtime;
    }

    private void sortGenresNamesByLength(){
        Collections.sort(mGenreNames, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                if (s.length() > t1.length())
                    return 1;
                else if (s.length() < t1.length())
                    return -1;
                else return 0;
            }
        });
    }


    public static List<MovieInfo> createMovieInfoList(int[] imageIDs, String[] names) {
        if (imageIDs.length != names.length) {
            throw new IllegalArgumentException("Length of arrays should be same");
        }
        List<MovieInfo> movieInfoList = new ArrayList<>();
        MovieInfo movieInfo;
        for (int i = 0; i < imageIDs.length; i++) {
            movieInfo = new MovieInfo(imageIDs[i]);
            movieInfo.mTitle = names[i];
            movieInfoList.add(movieInfo);
        }
        return movieInfoList;
    }
}
