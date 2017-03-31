package ru.surf.course.movierecommendations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import ru.surf.course.movierecommendations.models.Favorite;
import ru.surf.course.movierecommendations.models.RecommendedMovieGenres;
import ru.surf.course.movierecommendations.models.RecommendedTVShowsGenres;

/**
 * Created by Sergey on 26.03.2017.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "data_base";
    private static final int DATABASE_VERSION = 6;

    private static DBHelper sInstance;

    private Dao<Favorite, Integer> mFavoriteDao = null;
    private Dao<RecommendedMovieGenres, Integer> mRecommendedMovieGenresDao = null;
    private Dao<RecommendedTVShowsGenres, Integer> mRecommendedTVShowsGenresDao = null;


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    public static synchronized DBHelper getHelper(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context);
        }

        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Favorite.class);
            TableUtils.createTableIfNotExists(connectionSource, RecommendedMovieGenres.class);
            TableUtils.createTableIfNotExists(connectionSource, RecommendedTVShowsGenres.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Favorite.class, true);
            TableUtils.dropTable(connectionSource, RecommendedMovieGenres.class, true);
            TableUtils.dropTable(connectionSource, RecommendedTVShowsGenres.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        mFavoriteDao = null;
        super.close();
    }

    private Dao<Favorite, Integer> getFavoriteDao() {
        if (mFavoriteDao == null) {
            try {
                mFavoriteDao = getDao(Favorite.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mFavoriteDao;
    }

    private Dao<RecommendedMovieGenres, Integer> getRecommendedMovieGenresDao() {

        if (mRecommendedMovieGenresDao == null) {
            try {
                mRecommendedMovieGenresDao = getDao(RecommendedMovieGenres.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mRecommendedMovieGenresDao;
    }

    private Dao<RecommendedTVShowsGenres, Integer> getRecommendedTVShowsGenresDao() {
        if (mRecommendedTVShowsGenresDao == null) {
            try {
                mRecommendedTVShowsGenresDao = getDao(RecommendedTVShowsGenres.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mRecommendedTVShowsGenresDao;
    }

    public void addFavorite(Favorite favorite) {
        try {
            getFavoriteDao().create(favorite);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Favorite> getAllFavorites() {
        List<Favorite> result = null;
        try {
            result = getFavoriteDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addRecommendedMovieGenre(int id) {
        RecommendedMovieGenres recommendedMovieGenres;
        try {
            recommendedMovieGenres = new RecommendedMovieGenres();
            recommendedMovieGenres.setGenre_id(id);
            getRecommendedMovieGenresDao().create(recommendedMovieGenres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRecommendedTVShowGenre(int id) {
        RecommendedTVShowsGenres recommendedTVShowsGenres;
        try {
            recommendedTVShowsGenres = new RecommendedTVShowsGenres();
            recommendedTVShowsGenres.setGenre_id(id);
            getRecommendedTVShowsGenresDao().create(recommendedTVShowsGenres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RecommendedMovieGenres> getAllRecommendedMovieGenres() {
        List<RecommendedMovieGenres> result = null;
        try {
            result = getRecommendedMovieGenresDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<RecommendedTVShowsGenres> getAllRecommendedTVShowGenres() {
        List<RecommendedTVShowsGenres> result = null;
        try {
            result = getRecommendedTVShowsGenresDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
