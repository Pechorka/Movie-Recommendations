package ru.surf.course.movierecommendations.domain;

import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = RecommendedTVShowsGenres.TABLE_NAME_RECOMMENDED_TVSHOWS_GENRES)
public class RecommendedTVShowsGenres extends RecommendedGenres {
    static final String TABLE_NAME_RECOMMENDED_TVSHOWS_GENRES = "recommended_tvshows_genres";
}
