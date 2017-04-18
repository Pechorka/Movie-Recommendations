package ru.surf.course.movierecommendations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import ru.surf.course.movierecommendations.models.CustomFilter;
import ru.surf.course.movierecommendations.models.Favorite;
import ru.surf.course.movierecommendations.models.Genre;
import ru.surf.course.movierecommendations.models.MovieGenre;
import ru.surf.course.movierecommendations.models.RecommendedMovieGenres;
import ru.surf.course.movierecommendations.models.RecommendedTVShowsGenres;
import ru.surf.course.movierecommendations.models.TVShowGenre;

/**
 * Created by Sergey on 26.03.2017.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

  private static final String DATABASE_NAME = "data_base";
  private static final int DATABASE_VERSION = 20;

  private static DBHelper sInstance;

  private Dao<Favorite, Integer> mFavoriteDao = null;
  private Dao<RecommendedMovieGenres, Integer> mRecommendedMovieGenresDao = null;
  private Dao<RecommendedTVShowsGenres, Integer> mRecommendedTVShowsGenresDao = null;
  private Dao<CustomFilter, Integer> mCustomFilterDao = null;
  private Dao<MovieGenre, Integer> mMovieGenresDao = null;
  private Dao<TVShowGenre, Integer> mTVShowGenresDao = null;


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
      TableUtils.createTable(connectionSource, RecommendedMovieGenres.class);
      TableUtils.createTable(connectionSource, RecommendedTVShowsGenres.class);
      TableUtils.createTable(connectionSource, CustomFilter.class);
      TableUtils.createTable(connectionSource, MovieGenre.class);
      TableUtils.createTable(connectionSource, TVShowGenre.class);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
      int newVersion) {
    try {
      TableUtils.dropTable(connectionSource, Favorite.class, true);
      TableUtils.dropTable(connectionSource, RecommendedMovieGenres.class, true);
      TableUtils.dropTable(connectionSource, RecommendedTVShowsGenres.class, true);
      TableUtils.dropTable(connectionSource, CustomFilter.class, true);
      TableUtils.dropTable(connectionSource, Genre.class, true);
      TableUtils.dropTable(connectionSource, MovieGenre.class, true);
      TableUtils.dropTable(connectionSource, TVShowGenre.class, true);
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


  private Dao<CustomFilter, Integer> getCustomFilterDao() {
    if (mCustomFilterDao == null) {
      try {
        mCustomFilterDao = getDao(CustomFilter.class);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return mCustomFilterDao;
  }

  private Dao<MovieGenre, Integer> getMovieGenresDao() {
    if (mMovieGenresDao == null) {
      try {
        mMovieGenresDao = getDao(MovieGenre.class);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return mMovieGenresDao;
  }

  private Dao<TVShowGenre, Integer> getTVShowGenresDao() {
    if (mTVShowGenresDao == null) {
      try {
        mTVShowGenresDao = getDao(TVShowGenre.class);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return mTVShowGenresDao;
  }

  public void addFavorite(Favorite favorite) {
    try {
      getFavoriteDao().create(favorite);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Favorite getFavoriteByMediaId(int id) {
    Favorite result = null;
    QueryBuilder<Favorite, Integer> queryBuilder = getFavoriteDao().queryBuilder();
    try {
      queryBuilder.where().eq(Favorite.FIELD_NAME_MEDIA_ID, id);
      result = queryBuilder.queryForFirst();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void deleteFavorite(Favorite favorite) {
    try {
      getFavoriteDao().delete(favorite);
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

  public void addCustomFilter(CustomFilter customFilter) {
    try {
      getCustomFilterDao().create(customFilter);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<CustomFilter> getAllCustomFilters() {
    List<CustomFilter> result = null;
    try {
      result = getCustomFilterDao().queryForAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void addMovieGenre(MovieGenre genre) {
    try {
      getMovieGenresDao().create(genre);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addMovieGenres(List<MovieGenre> genres) {
    Dao<MovieGenre, Integer> dao = getMovieGenresDao();
    try {
      for (MovieGenre genre : genres) {
        dao.create(genre);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<MovieGenre> getAllMovieGenres() {
    List<MovieGenre> genres = null;
    try {
      genres = getMovieGenresDao().queryForAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return genres;
  }

  public <T extends Genre> void updateMovieGenre(T genre) {
    try {
      getMovieGenresDao().update((MovieGenre) genre);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void clearMoiveGenres() {
    try {
      TableUtils.clearTable(getConnectionSource(), MovieGenre.class);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addTVShowGenre(TVShowGenre genre) {
    try {
      getTVShowGenresDao().create(genre);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addTVShowGenres(List<TVShowGenre> genres) {
    Dao<TVShowGenre, Integer> dao = getTVShowGenresDao();
    try {
      for (TVShowGenre genre : genres) {
        dao.create(genre);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  public List<TVShowGenre> getAllTVShowGenres() {
    List<TVShowGenre> genres = null;
    try {
      genres = getTVShowGenresDao().queryForAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return genres;
  }

  public void updateTVShowGenre(TVShowGenre genre) {
    try {
      getTVShowGenresDao().update(genre);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void clearTVShowGenres() {
    try {
      TableUtils.clearTable(getConnectionSource(), TVShowGenre.class);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}