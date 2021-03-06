package ru.surf.course.movierecommendations.domain.people;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;
import ru.surf.course.movierecommendations.domain.Media;

/**
 * Created by andrew on 1/29/17.
 */
@Data
public class CrewMember extends Credit implements Serializable {

  @SerializedName("department")
  private String department;
  @SerializedName("job")
  private String job;

  public CrewMember(String creditId, Person person, String department, String job) {
    super(creditId, person);
    this.department = department;
    this.job = job;
  }

  public CrewMember(String creditId, Media media, String department, String job) {
    super(creditId, media);
    this.department = department;
    this.job = job;
  }

}
