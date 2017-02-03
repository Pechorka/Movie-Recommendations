package ru.surf.course.movierecommendations.models;

import java.util.Date;

/**
 * Created by andrew on 1/29/17.
 */

public class Person {

    protected enum Gender {MALE, FEMALE}

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

    public Person(String name, int id, String profilePath) {
        mName = name;
        mId = id;
        mProfilePath = profilePath;
    }

    public Person(String mName, int mId, String mProfilePath, boolean mAdult, String mBiography, Date mBirthday, Date mDeathday, Gender mGender, String mImdbId, String mPlaceOfBirth, Double mPopularity) {
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
}
