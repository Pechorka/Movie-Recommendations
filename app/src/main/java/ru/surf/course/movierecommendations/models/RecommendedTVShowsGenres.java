package ru.surf.course.movierecommendations.models;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Sergey on 30.03.2017.
 */

@DatabaseTable(tableName = RecommendedTVShowsGenres.TABLE_NAME_RECOMMENDED_TVSHOWS_GENRES)
public class RecommendedTVShowsGenres extends RecommendedGenres {
  public static final String TABLE_NAME_RECOMMENDED_TVSHOWS_GENRES = "recommended_tvshows_genres";
}
