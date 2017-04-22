package ru.surf.course.movierecommendations.domain.people;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Data;
import ru.surf.course.movierecommendations.domain.TmdbImage;


@Data
public class Person implements Serializable {


    public final String LOG_TAG = getClass().getSimpleName();

    protected String name;
    protected int id;
    protected String profilePath;
    protected boolean adult;
    protected String biography;
    protected Date birthday;
    protected Date deathday;
    protected Gender gender;
    protected String imdbId;
    protected String placeOfBirth;
    protected Double popularity;
    protected Locale infoLanguage;
    protected List<TmdbImage> profilePictures;
    protected List<Credit> credits;

    public Person(int id) {
        this.id = id;
    }

    public Person(String name, int id, String profilePath) {
        this.name = name;
        this.id = id;
        this.profilePath = profilePath;
    }

    public Person(String name, int id, String profilePath, boolean adult, String biography,
                  Date birthday, Date deathday, Gender gender, String imdbId, String placeOfBirth,
                  Double popularity) {
        this.name = name;
        this.id = id;
        this.profilePath = profilePath;
        this.adult = adult;
        this.biography = biography;
        this.birthday = birthday;
        this.deathday = deathday;
        this.gender = gender;
        this.imdbId = imdbId;
        this.placeOfBirth = placeOfBirth;
        this.popularity = popularity;
    }

    public Person(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void fillFields(Object from) {
        Field[] fields = from.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Field fieldFrom = from.getClass().getDeclaredField(field.getName());
                Object value = fieldFrom.get(from);
                this.getClass().getDeclaredField(field.getName()).set(this, value);
            } catch (IllegalAccessException e) {
                Log.d(LOG_TAG, "Copy error" + e.getMessage());
            } catch (NoSuchFieldException e) {
                Log.d(LOG_TAG, "Copy error" + e.getMessage());
            }
        }
    }

    public enum Gender {
        UNKNOWN,
        FEMALE,
        MALE
    }

}
