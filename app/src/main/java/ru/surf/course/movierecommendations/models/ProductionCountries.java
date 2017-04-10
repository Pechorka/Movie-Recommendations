package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

import java.io.Serializable;

/**
 * Created by sergey on 03.04.17.
 */

@DatabaseTable(tableName = ProductionCountries.TABLE_NAME_PRODUCTION_COUNTRIES)
public class ProductionCountries implements Serializable {

  public static final String TABLE_NAME_PRODUCTION_COUNTRIES = "production_countries";
  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_REGION = "region";
  public static final String FIELD_NAME_COUNTRIE_NAME = "countrie_name";
  public static final String FIELD_NAME_MEDIA = "media";

  @DatabaseField(columnName = FIELD_NAME_COUNTRIE_NAME)
  @SerializedName("name")
  private String name;

  @DatabaseField(columnName = FIELD_NAME_REGION)
  @SerializedName("iso_3166_1")
  private String region;

  @DatabaseField(columnName = FIELD_NAME_MEDIA,foreign = true)
  private Media mMedia;

  @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
  private int mId;

  public ProductionCountries(){}

  public ProductionCountries(String name, String region) {
    this.name = name;
    this.region = region;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public Media getmMedia() {
    return mMedia;
  }

  public void setmMedia(Media mMedia) {
    this.mMedia = mMedia;
  }

  public int getmId() {
    return mId;
  }

  public void setmId(int mId) {
    this.mId = mId;
  }
}
