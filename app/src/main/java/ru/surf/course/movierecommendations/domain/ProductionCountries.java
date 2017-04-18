package ru.surf.course.movierecommendations.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by sergey on 03.04.17.
 */

public class ProductionCountries implements Serializable {

  @SerializedName("name")
  private String name;

  @SerializedName("iso_3166_1")
  private String region;

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
