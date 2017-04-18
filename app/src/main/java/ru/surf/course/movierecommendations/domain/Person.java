package ru.surf.course.movierecommendations.domain;

import android.util.Log;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by andrew on 1/29/17.
 */

public class Person implements Serializable {


  public final String LOG_TAG = getClass().getSimpleName();

  protected String mName;
  protected int mId;
  protected String mProfilePath;
  protected boolean mAdult;
  protected String mBiography;
  protected Date mBirthday;
  protected Date mDeathday;
  protected Gender mGender;
  protected String mImdbId;
  protected String mPlaceOfBirth;
  protected Double mPopularity;
  protected Locale mInfoLanguage;
  protected List<TmdbImage> mProfilePictures;
  protected List<Credit> mCredits;
  public Person(int id) {
    mId = id;
  }

  public Person(String name, int id, String profilePath) {
    mName = name;
    mId = id;
    mProfilePath = profilePath;
  }

  public Person(String mName, int mId, String mProfilePath, boolean mAdult, String mBiography,
      Date mBirthday, Date mDeathday, Gender mGender, String mImdbId, String mPlaceOfBirth,
      Double mPopularity) {
    this.mName = mName;
    this.mId = mId;
    this.mProfilePath = mProfilePath;
    this.mAdult = mAdult;
    this.mBiography = mBiography;
    this.mBirthday = mBirthday;
    this.mDeathday = mDeathday;
    this.mGender = mGender;
    this.mImdbId = mImdbId;
    this.mPlaceOfBirth = mPlaceOfBirth;
    this.mPopularity = mPopularity;
  }

  public Person(String mName, int mId) {
    this.mName = mName;
    this.mId = mId;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    this.mName = name;
  }

  public String getProfilePath() {
    return mProfilePath;
  }

  public void setProfilePath(String profilePath) {
    this.mProfilePath = profilePath;
  }

  public int getId() {
    return mId;
  }

  public void setId(int id) {
    this.mId = id;
  }

  public boolean isAdult() {
    return mAdult;
  }

  public void setAdult(boolean adult) {
    this.mAdult = adult;
  }

  public String getBiography() {
    return mBiography;
  }

  public void setBiography(String biography) {
    this.mBiography = biography;
  }

  public Date getBirthday() {
    return mBirthday;
  }

  public void setBirthday(Date birthday) {
    this.mBirthday = birthday;
  }

  public Date getDeathday() {
    return mDeathday;
  }

  public void setDeathday(Date deathday) {
    this.mDeathday = deathday;
  }

  public Gender getGender() {
    return mGender;
  }

  public void setGender(Gender gender) {
    this.mGender = gender;
  }

  public String getImdbId() {
    return mImdbId;
  }

  public void setImdbId(String imdbId) {
    this.mImdbId = imdbId;
  }

  public String getPlaceOfBirth() {
    return mPlaceOfBirth;
  }

  public void setPlaceOfBirth(String placeOfBirth) {
    this.mPlaceOfBirth = placeOfBirth;
  }

  public Double getPopularity() {
    return mPopularity;
  }

  public void setPopularity(Double popularity) {
    this.mPopularity = popularity;
  }

  public Locale getInfoLanguage() {
    return mInfoLanguage;
  }

  public void setInfoLanguage(Locale infoLanguage) {
    mInfoLanguage = infoLanguage;
  }

  public List<TmdbImage> getProfilePictures() {
    return mProfilePictures;
  }

  public void setProfilePictures(List<TmdbImage> profilePictures) {
    mProfilePictures = profilePictures;
  }

  public List<Credit> getCredits() {
    return mCredits;
  }

  public void setCredits(List<Credit> credits) {
    mCredits = credits;
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

  public enum Gender {
    UNKNOWN,
    FEMALE,
    MALE
  }

}
