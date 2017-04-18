package ru.surf.course.movierecommendations.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by andrew on 2/3/17.
 */

public class Credit implements Serializable {


  @SerializedName("id")
  protected String mCreditId;

  @SerializedName("person")
  protected Person mPerson;

  protected Media mMedia;



  public Credit(String creditId, Person person) {
    mCreditId = creditId;
    mPerson = person;
  }

  public Credit(String creditId, Media media) {
    mCreditId = creditId;
    mMedia = media;
  }

  public Credit(String creditId, Person person, Media media) {
    this.mCreditId = creditId;
    this.mPerson = person;
    this.mMedia = media;
  }


  public String getCreditId() {
    return mCreditId;
  }

  public void setCreditId(String creditId) {
    mCreditId = creditId;
  }

  public Person getPerson() {
    return mPerson;
  }

  public void setPerson(Person person) {
    mPerson = person;
  }

  public Media getMedia() {
    return mMedia;
  }

  public void setMedia(Media media) {
    this.mMedia = media;
  }

}
