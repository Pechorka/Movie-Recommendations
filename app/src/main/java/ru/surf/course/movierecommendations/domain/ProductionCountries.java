package ru.surf.course.movierecommendations.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductionCountries implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("iso_3166_1")
    private String region;

    public ProductionCountries(String name, String region) {
        this.name = name;
        this.region = region;
    }
}
