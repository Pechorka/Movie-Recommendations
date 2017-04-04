package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sergey on 03.04.17.
 */

public class ProductionCompanies implements Serializable{

  @SerializedName("name")
  private String name;
  @SerializedName("id")
  private int id;

  public ProductionCompanies(){}

  public ProductionCompanies(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
