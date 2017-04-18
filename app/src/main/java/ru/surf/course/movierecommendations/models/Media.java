package ru.surf.course.movierecommendations.models;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by andrew on 08-Feb-17.
 */

public class Media implements Serializable {

  private final String LOG_TAG = getClass().getSimpleName();


  @SerializedName("title")
  protected String mTitle;

  @SerializedName("original_title")
  protected String mOriginalTitle;

  @SerializedName("original_language")
  protected Locale mOriginalLanguage;

  @SerializedName("genres")
  protected ArrayList<Genre> mGenres;

  @SerializedName("genre_ids")
  protected ArrayList<Integer> mGenresIds;

  @SerializedName("poster_path")
  protected String mPosterPath;

  protected List<TmdbImage> mBackdrops;

  protected Bitmap mPosterBitmap;

  @SerializedName("overview")
  protected String mOverview;

  @SerializedName("release_date")
  protected Date mDate;

  @SerializedName("backdrop_path")
  protected String mBackdropPath;

  @SerializedName("vote_average")
  protected Double mVoteAverage;

  @SerializedName("vote_count")
  protected int mVoteCount;

  @SerializedName("id")
  protected int mMediaId;

  @SerializedName("budget")
  protected String mBudget;

  @SerializedName("production_companies")
  protected ArrayList<ProductionCompanies> mProductionCompanies;

  @SerializedName("production_countries")
  protected ArrayList<ProductionCountries> mProductionCountries;

  protected Locale mInfoLanguage;

  @SerializedName("status")
  protected String mStatus;

  @SerializedName("credits")
  protected ArrayList<Credit> mCredits;

  @SerializedName("reviews")
  protected ArrayList<Review> mReviews;


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

  public void setGenres(ArrayList<Genre> genres) {
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

  public void setmGenresIds(ArrayList<Integer> mGenresIds) {
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

  public void setProductionCompaniesNames(ArrayList<ProductionCompanies> productionCompanies) {
    this.mProductionCompanies = productionCompanies;
  }

  public List<ProductionCountries> getProductionCountries() {
    return mProductionCountries;
  }

  public void setProductionCountriesNames(ArrayList<ProductionCountries> productionCountries) {
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

  public void setCredits(ArrayList<Credit> credits) {
    this.mCredits = credits;
  }

  public List<Review> getReviews() {
    return mReviews;
  }

  public void setReviews(ArrayList<Review> reviews) {
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