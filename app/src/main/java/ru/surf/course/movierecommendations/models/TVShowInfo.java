package ru.surf.course.movierecommendations.models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sergey on 07.02.2017.
 */

public class TVShowInfo {

    public String title;
    public String originalTitle;
    public Locale originalLanguage;
    public List<Integer> genreIds;
    public String posterPath;
    public List<TmdbImage> backdrops;
    public Bitmap posterBitmap;
    public String overview;
    public Date date;
    public String backdropPath;
    public Double voteAverage;
    public int voteCount;
    public int id;
    public String budget;
    public List<String> genreNames;
    public List<String> productionCompaniesNames;
    public String status;
    public List<Double> episode_runtime;
    public int number_of_episodes;
    public int number_of_seasons;
    public String type;
}
