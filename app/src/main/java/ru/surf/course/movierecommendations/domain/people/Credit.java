package ru.surf.course.movierecommendations.domain.people;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;
import ru.surf.course.movierecommendations.domain.Media;


@Data
public class Credit implements Serializable {


    @SerializedName("id")
    protected String creditId;

    @SerializedName("person")
    protected Person person;

    protected Media media;

    Credit(String creditId, Person person) {
        this.creditId = creditId;
        this.person = person;
    }

    Credit(String creditId, Media media) {
        this.creditId = creditId;
        this.media = media;
    }

    public Credit(String creditId, Person person, Media media) {
        this.creditId = creditId;
        this.person = person;
        this.media = media;
    }

}
