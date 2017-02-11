package ru.surf.course.movierecommendations.models;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.models.Credit;
import ru.surf.course.movierecommendations.models.TmdbImage;

/**
 * Created by andrew on 08-Feb-17.
 */

public class Media implements Serializable {

    private final String LOG_TAG = getClass().getSimpleName();

    protected String mTitle;
    protected String mOriginalTitle;
    protected Locale mOriginalLanguage;
    protected List<Integer> mGenreIds;
    protected String mPosterPath;
    protected List<TmdbImage> mBackdrops;
    protected Bitmap mPosterBitmap;
    protected String mOverview;
    protected Date mDate;
    protected String mBackdropPath;
    protected Double mVoteAverage;
    protected int mVoteCount;
    protected int mId;
    protected String mBudget;
    protected List<String> mGenreNames;
    protected List<String> mProductionCompaniesNames;
    protected List<String> mProductionCountriesNames;
    protected Locale mInfoLanguage;
    protected String mStatus;
    protected List<Credit> mCredits;


    public Media(int mId) {
        this.mId = mId;
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

    public List<Integer> getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.mGenreIds = genreIds;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getBudget() {
        return mBudget;
    }

    public void setBudget(String budget) {
        this.mBudget = budget;
    }

    public List<String> getGenreNames() {
        return mGenreNames;
    }

    public void setGenreNames(List<String> genreNames) {
        this.mGenreNames = genreNames;
    }

    public List<String> getProductionCompaniesNames() {
        return mProductionCompaniesNames;
    }

    public void setProductionCompaniesNames(List<String> productionCompaniesNames) {
        this.mProductionCompaniesNames = productionCompaniesNames;
    }

    public List<String> getProductionCountriesNames() {
        return mProductionCountriesNames;
    }

    public void setProductionCountriesNames(List<String> productionCountriesNames) {
        this.mProductionCountriesNames = productionCountriesNames;
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
