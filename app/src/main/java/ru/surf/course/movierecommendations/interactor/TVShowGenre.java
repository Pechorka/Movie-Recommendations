package ru.surf.course.movierecommendations.interactor;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import ru.surf.course.movierecommendations.domain.genre.Genre;

@DatabaseTable(tableName = TVShowGenre.TABLE_NAME_TVSHOW_GENRES)
public class TVShowGenre extends Genre {

    static final String TABLE_NAME_TVSHOW_GENRES = "tvshow_genres";

    public static class RetrofitResult {

        @SerializedName("genres")
        public List<TVShowGenre> tvShowGenres;
    }

}
