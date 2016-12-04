package ru.surf.course.movierecommendations;

import java.io.Serializable;
import java.util.Date;

public class MovieInfo implements Serializable{

    public String title;
    public String posterPath;
    public String overview;
    public Date date;
    public String backdropPath;
    public Double rating;
    public int voteCount;
    public int id;

    public MovieInfo(){

    }

    public MovieInfo(String title, String posterPath, String overview, Date date, String backdropPath, Double rating, int voteCount, int id) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.date = date;
        this.backdropPath = backdropPath;
        this.rating = rating;
        this.voteCount = voteCount;
        this.id = id;
    }
}
