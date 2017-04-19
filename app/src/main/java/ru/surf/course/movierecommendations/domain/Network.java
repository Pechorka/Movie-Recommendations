package ru.surf.course.movierecommendations.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;

/**
 * Created by Sergey on 17.02.2017.
 */
@Data
public class Network implements Serializable {

  @SerializedName("id")
  private int id;
  @SerializedName("name")
  private String name;

}
