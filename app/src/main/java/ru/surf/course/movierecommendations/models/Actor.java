package ru.surf.course.movierecommendations.models;

/**
 * Created by andrew on 1/11/17.
 */

public class Actor extends People {

    private int mCast_id;
    private String mCharacter;
    private String mCredit_id;
    private int mOrder;
    private String mName;
    private int mId;
    private String mProfilePath;

    public Actor(String name, int id, String profilePath, int mCast_id, String mCharacter, String mCredit_id, int mOrder, String mName, int mId, String mProfilePath) {
        super(name, id, profilePath);
        this.mCast_id = mCast_id;
        this.mCharacter = mCharacter;
        this.mCredit_id = mCredit_id;
        this.mOrder = mOrder;
        this.mName = mName;
        this.mId = mId;
        this.mProfilePath = mProfilePath;
    }

    public int getmCast_id() {
        return mCast_id;
    }

    public void setmCast_id(int mCast_id) {
        this.mCast_id = mCast_id;
    }

    public String getmCharacter() {
        return mCharacter;
    }

    public void setmCharacter(String mCharacter) {
        this.mCharacter = mCharacter;
    }

    public String getmCredit_id() {
        return mCredit_id;
    }

    public void setmCredit_id(String mCredit_id) {
        this.mCredit_id = mCredit_id;
    }

    public int getmOrder() {
        return mOrder;
    }

    public void setmOrder(int mOrder) {
        this.mOrder = mOrder;
    }

    @Override
    public String getmName() {
        return mName;
    }

    @Override
    public void setmName(String mName) {
        this.mName = mName;
    }

    @Override
    public int getmId() {
        return mId;
    }

    @Override
    public void setmId(int mId) {
        this.mId = mId;
    }

    @Override
    public String getmProfilePath() {
        return mProfilePath;
    }

    @Override
    public void setmProfilePath(String mProfilePath) {
        this.mProfilePath = mProfilePath;
    }
}
