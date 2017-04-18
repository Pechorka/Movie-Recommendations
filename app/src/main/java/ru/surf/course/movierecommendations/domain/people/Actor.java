package ru.surf.course.movierecommendations.domain.people;

import java.io.Serializable;

import ru.surf.course.movierecommendations.domain.Media;

/**
 * Created by andrew on 1/11/17.
 */

public class Actor extends Credit implements Serializable {

  private int mCastId;
  private String mCharacter;
  private int mOrder;

  public Actor(String creditId, Person person, int mCastId, String mCharacter, int mOrder) {
    super(creditId, person);
    this.mCastId = mCastId;
    this.mCharacter = mCharacter;
    this.mOrder = mOrder;
  }

  public Actor(String creditId, Media media, int castId, String character, int order) {
    super(creditId, media);
    mCastId = castId;
    mCharacter = character;
    mOrder = order;
  }

  public Actor(String creditId, Media media, String character) {
    super(creditId, media);
    mCharacter = character;
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
