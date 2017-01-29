package ru.surf.course.movierecommendations.models;

/**
 * Created by andrew on 1/11/17.
 */

public class Actor extends People {

    private int mCast_id;
    private String mCharacter;
    private String mCredit_id;
    private int mOrder;

    public Actor(String name, int id, String profilePath, int mCast_id, String mCharacter, String mCredit_id, int mOrder, String mName, int mId, String mProfilePath) {
        super(name, id, profilePath);
        this.mCast_id = mCast_id;
        this.mCharacter = mCharacter;
        this.mCredit_id = mCredit_id;
        this.mOrder = mOrder;
    }

    public int getCast_id() {
        return mCast_id;
    }

    public void setCast_id(int mCast_id) {
        this.mCast_id = mCast_id;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(String mCharacter) {
        this.mCharacter = mCharacter;
    }

    public String getCredit_id() {
        return mCredit_id;
    }

    public void setCredit_id(String mCredit_id) {
        this.mCredit_id = mCredit_id;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int mOrder) {
        this.mOrder = mOrder;
    }

}
