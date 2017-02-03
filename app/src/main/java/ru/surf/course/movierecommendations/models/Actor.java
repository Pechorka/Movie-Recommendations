package ru.surf.course.movierecommendations.models;

/**
 * Created by andrew on 1/11/17.
 */

public class Actor extends Credit {

    private int mCastId;
    private String mCharacter;
    private int mOrder;

    public Actor(String creditId, Person person, int mCastId, String mCharacter, int mOrder) {
        super(creditId, person);
        this.mCastId = mCastId;
        this.mCharacter = mCharacter;
        this.mOrder = mOrder;
    }

    public int getCast_id() {
        return mCastId;
    }

    public void setCast_id(int mCast_id) {
        this.mCastId = mCast_id;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(String mCharacter) {
        this.mCharacter = mCharacter;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int mOrder) {
        this.mOrder = mOrder;
    }

}
