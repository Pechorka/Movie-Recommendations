package ru.surf.course.movierecommendations.domain.people;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;
import ru.surf.course.movierecommendations.domain.Media;

/**
 * Created by andrew on 2/3/17.
 */
@Data
public class Credit implements Serializable {


  @SerializedName("id")
  protected String creditId;

  @SerializedName("person")
  protected Person person;

  protected Media media;

  public Credit(String creditId, Person person) {
    this.creditId = creditId;
    this.person = person;
  }

  public Credit(String creditId, Media media) {
    this.creditId = creditId;
    this.media = media;
  }

  public Credit(String creditId, Person person, Media media) {
    this.creditId = creditId;
    this.person = person;
    this.media = media;
  }

}
