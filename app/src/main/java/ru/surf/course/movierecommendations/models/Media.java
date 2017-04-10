package ru.surf.course.movierecommendations.models;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by andrew on 08-Feb-17.
 */

public class Media implements Serializable {

  private final String LOG_TAG = getClass().getSimpleName();

  public static final String FIELD_NAME_TITLE = "title";
  public static final String FIELD_NAME_ORIGINAL_TITLE = "original_title";
  public static final String FIELD_NAME_ORIGINAL_LANGUAGE = "original_language";
  public static final String FIELD_NAME_GENRES = "genres_info";
  public static final String FIELD_NAME_GENRE_IDS = "genre_ids";
  public static final String FIELD_NAME_POSTER_PATH = "poster_path";
  public static final String FIELD_NAME_OVERVIEW = "overview";
  public static final String FIELD_NAME_RELEASE_DATE = "release_date";
  public static final String FIELD_NAME_BACKDROP_PATH = "backdrop_path";
  public static final String FIELD_NAME_VOTE_AVERAGE = "vote_average";
  public static final String FIELD_NAME_VOTE_COUNT = "vote_count";
  public static final String FIELD_NAME_MEDIA_ID = "media_id";
  public static final String FIELD_NAME_BUDGET = "budget";
  public static final String FIELD_NAME_PRODUCTION_COMPANIES = "production_companies";
  public static final String FIELD_NAME_PRODUCTION_COUNTRIES = "production_countries";
  public static final String FIELD_NAME_STATUS = "status";
  public static final String FIELD_NAME_CREDITS = "credits";


  public Media(){}

  @DatabaseField(columnName = FIELD_NAME_TITLE)
  @SerializedName("title")
  protected String mTitle;

  @DatabaseField(columnName = FIELD_NAME_ORIGINAL_TITLE)
  @SerializedName("original_title")
  protected String mOriginalTitle;

  @DatabaseField(columnName = FIELD_NAME_ORIGINAL_LANGUAGE,dataType = DataType.SERIALIZABLE)
  @SerializedName("original_language")
  protected Locale mOriginalLanguage;

//  @ForeignCollectionField(foreignFieldName = Genre.FIELD_NAME_MEDIA)
//  @DatabaseField(columnName = FIELD_NAME_GENRES)
  @SerializedName("genres")
  protected List<Genre> mGenres;

//  @DatabaseField(columnName = FIELD_NAME_GENRE_IDS)
  @SerializedName("genre_ids")
  protected List<Integer> mGenresIds;

  @DatabaseField(columnName = FIELD_NAME_POSTER_PATH)
  @SerializedName("poster_path")
  protected String mPosterPath;

  protected List<TmdbImage> mBackdrops;

  protected Bitmap mPosterBitmap;

  @DatabaseField(columnName = FIELD_NAME_OVERVIEW)
  @SerializedName("overview")
  protected String mOverview;

  @DatabaseField(columnName = FIELD_NAME_RELEASE_DATE)
  @SerializedName("release_date")
  protected Date mDate;

  @DatabaseField(columnName = FIELD_NAME_BACKDROP_PATH)
  @SerializedName("backdrop_path")
  protected String mBackdropPath;

  @DatabaseField(columnName = FIELD_NAME_VOTE_AVERAGE)
  @SerializedName("vote_average")
  protected Double mVoteAverage;

  @DatabaseField(columnName = FIELD_NAME_VOTE_COUNT)
  @SerializedName("vote_count")
  protected int mVoteCount;

  @DatabaseField(columnName = FIELD_NAME_MEDIA_ID)
  @SerializedName("id")
  protected int mMediaId;

  @DatabaseField(columnName = FIELD_NAME_BUDGET)
  @SerializedName("budget")
  protected String mBudget;

//  @ForeignCollectionField(foreignFieldName = ProductionCompanies.FIELD_NAME_MEDIA)
//  @DatabaseField(columnName = FIELD_NAME_PRODUCTION_COMPANIES)
  @SerializedName("production_companies")
  protected List<ProductionCompanies> mProductionCompanies;

//  @ForeignCollectionField(foreignFieldName = ProductionCountries.FIELD_NAME_MEDIA)
//  @DatabaseField(columnName = FIELD_NAME_PRODUCTION_COUNTRIES)
  @SerializedName("production_countries")
  protected List<ProductionCountries> mProductionCountries;

  protected Locale mInfoLanguage;

  @DatabaseField(columnName = FIELD_NAME_STATUS)
  @SerializedName("status")
  protected String mStatus;

//  @ForeignCollectionField(foreignFieldName = Credit.FIELD_NAME_MEDIA)
//  @DatabaseField(columnName = FIELD_NAME_CREDITS)
  @SerializedName("credits")
  protected List<Credit> mCredits;

//  @ForeignCollectionField(foreignFieldName = Review.FIELD_NAME_MEDIA)
//  @DatabaseField(columnName = FIELD_NAME_REVIEWS)
  @SerializedName("reviews")
  protected List<Review> mReviews;


  public Media(int mMediaId) {
    this.mMediaId = mMediaId;
  }


  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    this.mTitle = title;
  }

  public String getOriginalTitle() {
    return mOriginalTitle;
  }

  public void setOriginalTitle(String originalTitle) {
    this.mOriginalTitle = originalTitle;
  }

  public Locale getOriginalLanguage() {
    return mOriginalLanguage;
  }

  public void setOriginalLanguage(Locale originalLanguage) {
    this.mOriginalLanguage = originalLanguage;
  }

  public List<Genre> getGenres() {
    return mGenres;
  }

  public void setGenres(List<Genre> genres) {
    mGenres = genres;
  }

  public String getPosterPath() {
    return mPosterPath;
  }

  public void setPosterPath(String posterPath) {
    this.mPosterPath = posterPath;
  }

  public List<Integer> getmGenresIds() {
    return mGenresIds;
  }

  public void setmGenresIds(List<Integer> mGenresIds) {
    this.mGenresIds = mGenresIds;
  }

  public List<TmdbImage> getBackdrops() {
    return mBackdrops;
  }

  public void setBackdrops(List<TmdbImage> backdrops) {
    this.mBackdrops = backdrops;
  }

  public Bitmap getPosterBitmap() {
    return mPosterBitmap;
  }

  public void setPosterBitmap(Bitmap posterBitmap) {
    this.mPosterBitmap = posterBitmap;
  }

  public String getOverview() {
    return mOverview;
  }

  public void setOverview(String overview) {
    this.mOverview = overview;
  }

  public Date getDate() {
    return mDate;
  }

  public void setDate(Date date) {
    this.mDate = date;
  }

  public String getBackdropPath() {
    return mBackdropPath;
  }

  public void setBackdropPath(String backdropPath) {
    this.mBackdropPath = backdropPath;
  }

  public Double getVoteAverage() {
    return mVoteAverage;
  }

  public void setVoteAverage(Double voteAverage) {
    this.mVoteAverage = voteAverage;
  }

  public int getVoteCount() {
    return mVoteCount;
  }

  public void setVoteCount(int voteCount) {
    this.mVoteCount = voteCount;
  }

  public int getMediaId() {
    return mMediaId;
  }

  public void setMediaId(int id) {
    this.mMediaId = id;
  }

  public String getBudget() {
    return mBudget;
  }

  public void setBudget(String budget) {
    this.mBudget = budget;
  }

  public List<ProductionCompanies> getProductionCompanies() {
    return mProductionCompanies;
  }

  public void setProductionCompaniesNames(List<ProductionCompanies> productionCompanies) {
    this.mProductionCompanies = productionCompanies;
  }

  public List<ProductionCountries> getProductionCountries() {
    return mProductionCountries;
  }

  public void setProductionCountriesNames(List<ProductionCountries> productionCountries) {
    this.mProductionCountries = productionCountries;
  }

  public Locale getInfoLanguage() {
    return mInfoLanguage;
  }

  public void setInfoLanguage(Locale infoLanguage) {
    this.mInfoLanguage = infoLanguage;
  }

  public String getStatus() {
    return mStatus;
  }

  public void setStatus(String status) {
    this.mStatus = status;
  }

  public List<Credit> getCredits() {
    return mCredits;
  }

  public void setCredits(List<Credit> credits) {
    this.mCredits = credits;
  }

  public List<Review> getReviews() {
    return mReviews;
  }

  public void setReviews(List<Review> reviews) {
    mReviews = reviews;
  }

  public void fillFields(Object from) {
    Field[] fields = from.getClass().getDeclaredFields();
    for (Field field : fields) {
      try {
        Field fieldFrom = from.getClass().getDeclaredField(field.getName());
        Object value = fieldFrom.get(from);
        this.getClass().getDeclaredField(field.getName()).set(this, value);
      } catch (IllegalAccessException e) {
        Log.d(LOG_TAG, "Copy error" + e.getMessage());
      } catch (NoSuchFieldException e) {
        Log.d(LOG_TAG, "Copy error" + e.getMessage());
      }
    }
  }
}