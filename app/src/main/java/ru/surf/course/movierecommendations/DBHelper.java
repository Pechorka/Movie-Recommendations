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
import ru.surf.course.movierecommendations.models.Credit;
import ru.surf.course.movierecommendations.models.CustomFilter;
import ru.surf.course.movierecommendations.models.Favorite;
import ru.surf.course.movierecommendations.models.Genre;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.ProductionCompanies;
import ru.surf.course.movierecommendations.models.ProductionCountries;
import ru.surf.course.movierecommendations.models.RecommendedMovieGenres;
import ru.surf.course.movierecommendations.models.RecommendedTVShowsGenres;
import ru.surf.course.movierecommendations.models.Review;
import ru.surf.course.movierecommendations.models.TVShowInfo;

/**
 * Created by Sergey on 26.03.2017.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

  private static final String DATABASE_NAME = "data_base";
  private static final int DATABASE_VERSION = 10;

  private static DBHelper sInstance;

  private Dao<Favorite, Integer> mFavoriteDao = null;
  private Dao<RecommendedMovieGenres, Integer> mRecommendedMovieGenresDao = null;
  private Dao<RecommendedTVShowsGenres, Integer> mRecommendedTVShowsGenresDao = null;
  private Dao<MovieInfo, Integer> mMovieInfoDao = null;
  private Dao<TVShowInfo, Integer> mTVShowInfoDao = null;
  private Dao<CustomFilter, Integer> mCustomFilterDao = null;


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
//      TableUtils.createTableIfNotExists(connectionSource, Genre.class);
//      TableUtils.createTableIfNotExists(connectionSource, ProductionCountries.class);
//      TableUtils.createTableIfNotExists(connectionSource, ProductionCompanies.class);
//      TableUtils.createTableIfNotExists(connectionSource, Credit.class);
//      TableUtils.createTableIfNotExists(connectionSource, Review.class);
      TableUtils.createTableIfNotExists(connectionSource, CustomFilter.class);
      TableUtils.createTableIfNotExists(connectionSource, MovieInfo.class);
      TableUtils.createTableIfNotExists(connectionSource, TVShowInfo.class);
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
      TableUtils.dropTable(connectionSource, MovieInfo.class, true);
      TableUtils.dropTable(connectionSource,TVShowInfo.class,true);
//      TableUtils.dropTable(connectionSource,Genre.class,true);
//      TableUtils.dropTable(connectionSource,ProductionCompanies.class,true);
//      TableUtils.dropTable(connectionSource,ProductionCountries.class,true);
//      TableUtils.dropTable(connectionSource,Credit.class,true);
//      TableUtils.dropTable(connectionSource,Review.class,true);
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

  private Dao<MovieInfo, Integer> getMovieInfoDao() {
    if (mMovieInfoDao == null) {
      try {
        mMovieInfoDao = getDao(MovieInfo.class);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return mMovieInfoDao;
  }

  private Dao<TVShowInfo, Integer> getTVShowInfoDao() {
    if (mTVShowInfoDao == null) {
      try {
        mTVShowInfoDao = getDao(TVShowInfo.class);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return mTVShowInfoDao;
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

  public void deleteFavorite(Favorite favorite){
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

  public void addMovieInfo(MovieInfo movieInfo) {
    try {
      getMovieInfoDao().create(movieInfo);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public MovieInfo getMovieInfoByMovieId(int id) {
    MovieInfo result = null;
    QueryBuilder builder = getMovieInfoDao().queryBuilder();
    try {
      builder.where().eq(MovieInfo.FIELD_NAME_MEDIA_ID, id);
      result = (MovieInfo) builder.queryForFirst();
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
}
