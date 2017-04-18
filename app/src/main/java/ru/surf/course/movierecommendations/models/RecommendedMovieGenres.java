package ru.surf.course.movierecommendations.models;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Sergey on 30.03.2017.
 */

@DatabaseTable(tableName = RecommendedMovieGenres.TABLE_NAME_RECOMMENDED_MOVIE_GENRES)
public class RecommendedMovieGenres extends RecommendedGenres {

  public static final String TABLE_NAME_RECOMMENDED_MOVIE_GENRES = "recommended_movie_genres";
}
