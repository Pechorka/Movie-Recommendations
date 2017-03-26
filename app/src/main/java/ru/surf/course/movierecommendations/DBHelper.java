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

/**
 * Created by Sergey on 26.03.2017.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "data_base";
    private static final int DATABASE_VERSION = 1;

    private static DBHelper sInstance;

    private Dao<Favorite, Integer> mFavoriteDao = null;


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
            TableUtils.createTable(connectionSource, Favorite.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Favorite.class, true);
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
}
