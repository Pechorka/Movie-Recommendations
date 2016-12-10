package ru.surf.course.movierecommendations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieInfo implements Serializable{

    public String title;
    public String originalTitle;
    public List<Integer> genreIds;
    public String posterPath;
    public String overview;
    public Date date;
    public String backdropPath;
    public Double rating;
    public int voteCount;
    public int id;

    public MovieInfo(){

    }

    public MovieInfo(String title, String originalTitle, List<Integer> genreIds, String posterPath, String overview, Date date, String backdropPath, Double rating, int voteCount, int id) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.genreIds = genreIds;
        this.posterPath = posterPath;
        this.overview = overview;
        this.date = date;
        this.backdropPath = backdropPath;
        this.rating = rating;
        this.voteCount = voteCount;
        this.id = id;
    }


    public static List<MovieInfo> createMovieInfoList(int[] imageIDs, String[] names) {
        if (imageIDs.length != names.length) {
            throw new IllegalArgumentException("Length of arrays should be same");
        }
        List<MovieInfo> movieInfoList = new ArrayList<>();
        MovieInfo moviewInfo;
        for (int i = 0; i < imageIDs.length; i++) {
            moviewInfo = new MovieInfo();
            moviewInfo.id = imageIDs[i];
            moviewInfo.title = names[i];
            movieInfoList.add(moviewInfo);
        }
        return movieInfoList;
    }
}
