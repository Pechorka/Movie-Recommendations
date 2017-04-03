package ru.surf.course.movierecommendations.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by Sergey on 17.02.2017.
 */

public class Network implements Serializable {

  @SerializedName("id")
  private int id;
  @SerializedName("name")
  private String name;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
