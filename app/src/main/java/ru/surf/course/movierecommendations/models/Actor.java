package ru.surf.course.movierecommendations.models;

/**
 * Created by andrew on 1/11/17.
 */

public class Actor {

    public int cast_id;
    public String character;
    public String credit_id;
    public int id;
    public String name;
    public int order;
    public String profilePath;

    public Actor(int cast_id, String character, String credit_id, int id, String name, int order, String profilePath) {
        this.cast_id = cast_id;
        this.character = character;
        this.credit_id = credit_id;
        this.id = id;
        this.name = name;
        this.order = order;
        this.profilePath = profilePath;
    }
}
