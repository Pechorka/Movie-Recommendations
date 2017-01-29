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

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmProfilePath() {
        return mProfilePath;
    }

    public void setmProfilePath(String mProfilePath) {
        this.mProfilePath = mProfilePath;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }
}
