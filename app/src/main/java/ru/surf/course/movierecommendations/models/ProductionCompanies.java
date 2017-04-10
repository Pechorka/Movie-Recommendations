package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

/**
 * Created by sergey on 03.04.17.
 */

@DatabaseTable(tableName = ProductionCompanies.TABLE_NAME_PRODUCTION_COMPANIES)
public class ProductionCompanies implements Serializable{

  public static final String TABLE_NAME_PRODUCTION_COMPANIES = "production_companies";
  public static final String FIELD_NAME_ID = "id";
  public static final String FIELD_NAME_COMPANY_ID = "company_id";
  public static final String FIELD_NAME_COMPANY_NAME = "company_name";
  public static final String FIELD_NAME_MEDIA = "media";

  @DatabaseField(columnName = FIELD_NAME_COMPANY_NAME)
  @SerializedName("name")
  private String name;

  @DatabaseField(columnName = FIELD_NAME_COMPANY_ID)
  @SerializedName("id")
  private int companyId;

  @DatabaseField(columnName = FIELD_NAME_MEDIA,foreign = true)
  private Media mMedia;

  @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
  private int mId;

  public ProductionCompanies(){}

  public ProductionCompanies(String name, int id) {
    this.name = name;
    this.companyId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCompanyId() {
    return companyId;
  }

  public void setCompanyId(int id) {
    this.companyId = id;
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
