package ru.surf.course.movierecommendations.domain;

import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = RecommendedMovieGenres.TABLE_NAME_RECOMMENDED_MOVIE_GENRES)
public class RecommendedMovieGenres extends RecommendedGenres {

    static final String TABLE_NAME_RECOMMENDED_MOVIE_GENRES = "recommended_movie_genres";
}
