package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

/**
 * Created by andrew on 2/3/17.
 */

@DatabaseTable(tableName = Credit.TABLE_NAME_CREDITS)
public class Credit implements Serializable {

  public static final String TABLE_NAME_CREDITS = "credits";
  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_CREDIT_ID = "credit_id";
  public static final String FIELD_NAME_PERSON = "person";
  public static final String FIELD_NAME_MEDIA = "media";


  @DatabaseField(columnName = FIELD_NAME_CREDIT_ID)
  @SerializedName("id")
  protected String mCreditId;

//  @DatabaseField(columnName = FIELD_NAME_PERSON,foreign = true)
  @SerializedName("person")
  protected Person mPerson;

  @DatabaseField(columnName = FIELD_NAME_MEDIA,foreign = true)
  protected Media mMedia;


  @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
  private int mId;

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

  public int getmId() {
    return mId;
  }

  public void setmId(int mId) {
    this.mId = mId;
  }
}
