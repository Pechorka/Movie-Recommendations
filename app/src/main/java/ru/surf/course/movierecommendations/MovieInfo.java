package ru.surf.course.movierecommendations;

import java.util.Date;

/**
 * Created by andrew on 12/3/16.
 */

public class MovieInfo {

    public String name;
    public String posterPath;
    public String overview;
    public Date date;
    public String backdropPath;
    public Double rating;
    public int voteCount;
    public int id;

    public MovieInfo(){

    }

    public MovieInfo(String name, String posterPath, String overview, Date date, String backdropPath, Double rating, int voteCount, int id) {
        this.name = name;
        this.posterPath = posterPath;
        this.overview = overview;
        this.date = date;
        this.backdropPath = backdropPath;
        this.rating = rating;
        this.voteCount = voteCount;
        this.id = id;
    }
}
