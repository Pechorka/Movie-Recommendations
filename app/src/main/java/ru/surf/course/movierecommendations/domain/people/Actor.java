package ru.surf.course.movierecommendations.domain.people;

import java.io.Serializable;
import lombok.Data;
import ru.surf.course.movierecommendations.domain.Media;

/**
 * Created by andrew on 1/11/17.
 */
@Data
public class Actor extends Credit implements Serializable {

  private int castId;
  private String character;
  private int order;

  public Actor(String creditId, Person person, int castId, String character, int order) {
    super(creditId, person);
    this.castId = castId;
    this.character = character;
    this.order = order;
  }

  public Actor(String creditId, Media media, int castId, String character, int order) {
    super(creditId, media);
    this.castId = castId;
    this.character = character;
    this.order = order;
  }

  public Actor(String creditId, Media media, String character) {
    super(creditId, media);
    this.character = character;
  }

}
