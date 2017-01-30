package ru.surf.course.movierecommendations.models;

/**
 * Created by andrew on 1/29/17.
 */

public class People {

    protected String mName;
    protected int mId;
    protected String mProfilePath;

    public People() {
    }

    public People(String name, int id, String profilePath) {
        mName = name;
        mId = id;
        mProfilePath = profilePath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getProfilePath() {
        return mProfilePath;
    }

    public void setProfilePath(String mProfilePath) {
        this.mProfilePath = mProfilePath;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }
}
