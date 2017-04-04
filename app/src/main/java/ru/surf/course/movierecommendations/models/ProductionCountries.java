package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sergey on 03.04.17.
 */

public class ProductionCountries {

  @SerializedName("name")
  private String name;
  @SerializedName("iso_3166_1")
  private String region;

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
}