package ru.surf.course.movierecommendations.domain.people;

import java.io.Serializable;

import ru.surf.course.movierecommendations.domain.Media;

/**
 * Created by andrew on 1/29/17.
 */

public class CrewMember extends Credit implements Serializable {

  private String mDepartment;
  private String mJob;

  public CrewMember(String creditId, Person person, String mDepartment, String mJob) {
    super(creditId, person);
    this.mDepartment = mDepartment;
    this.mJob = mJob;
  }

  public CrewMember(String creditId, Media media, String department, String job) {
    super(creditId, media);
    mDepartment = department;
    mJob = job;
  }

  public String getDepartment() {
    return mDepartment;
  }

  public void setDepartment(String mDepartment) {
    this.mDepartment = mDepartment;
  }

  public String getJob() {
    return mJob;
  }

  public void setJob(String mJob) {
    this.mJob = mJob;
  }
}
