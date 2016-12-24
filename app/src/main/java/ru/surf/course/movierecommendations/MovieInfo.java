package ru.surf.course.movierecommendations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieInfo implements Serializable{

    public String title;
    public String originalTitle;
    public List<Integer> genreIds;
    public String posterPath;
    public String overview;
    public Date date;
    public String backdropPath;
    public Double voteAverage;
    public int voteCount;
    public int id;
    public String budget;
    public List<String> genreNames;
    public List<String> productionCompaniesNames;
    public List<String> productionCountriesNames;
    public String revenue;
    public Locale infoLanguage;

    public MovieInfo(){

    }

    public MovieInfo(String title, String originalTitle, List<Integer> genreIds, String posterPath, String overview, Date date, String backdropPath, Double voteAverage, int voteCount, int id) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.genreIds = genreIds;
        this.posterPath = posterPath;
        this.overview = overview;
        this.date = date;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.id = id;
    }
    //без даты
    public MovieInfo(String title, String originalTitle, List<Integer> genreIds, String posterPath, String overview, String backdropPath, Double voteAverage, int voteCount, int id) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.genreIds = genreIds;
        this.posterPath = posterPath;
        this.overview = overview;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.id = id;
    }


    public MovieInfo(String title, String originalTitle, List<Integer> genreIds, String posterPath, String overview, Date date, String backdropPath, Double voteAverage, int voteCount, int id, String budget, List<String> genreNames, List<String> productionCompaniesNames, List<String> productionCountriesNames, String revenue) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.genreIds = genreIds;
        this.posterPath = posterPath;
        this.overview = overview;
        this.date = date;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.id = id;
        this.budget = budget;
        this.genreNames = genreNames;
        this.productionCompaniesNames = productionCompaniesNames;
        this.productionCountriesNames = productionCountriesNames;
        this.revenue = revenue;

        sortGenresNamesByLength();
    }

    //без даты
    public MovieInfo(String title, String originalTitle, List<Integer> genreIds, String posterPath, String overview, String backdropPath, Double voteAverage, int voteCount, int id, String budget, List<String> genreNames, List<String> productionCompaniesNames, List<String> productionCountriesNames, String revenue) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.genreIds = genreIds;
        this.posterPath = posterPath;
        this.overview = overview;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.id = id;
        this.budget = budget;
        this.genreNames = genreNames;
        this.productionCompaniesNames = productionCompaniesNames;
        this.productionCountriesNames = productionCountriesNames;
        this.revenue = revenue;

        sortGenresNamesByLength();
    }

    private void sortGenresNamesByLength(){
        Collections.sort(genreNames, new Comparator<String>() {
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
            movieInfo = new MovieInfo();
            movieInfo.id = imageIDs[i];
            movieInfo.title = names[i];
            movieInfoList.add(movieInfo);
        }
        return movieInfoList;
    }
}
