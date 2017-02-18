package ru.surf.course.movierecommendations.models;

import java.io.Serializable;

/**
 * Created by andrew on 2/18/17.
 */

public class Genre implements Serializable {

    private int mId;
    private String mName;

    public Genre(int id, String name) {
        mId = id;
        mName = name;
    }

    public Genre(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
