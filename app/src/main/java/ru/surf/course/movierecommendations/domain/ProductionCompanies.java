package ru.surf.course.movierecommendations.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by sergey on 03.04.17.
 */

public class ProductionCompanies implements Serializable{

  @SerializedName("name")
  private String name;

  @SerializedName("id")
  private int companyId;

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

}
