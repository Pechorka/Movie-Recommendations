package ru.surf.course.movierecommendations.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductionCompanies implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int companyId;

    public ProductionCompanies(String name, int id) {
        this.name = name;
        this.companyId = id;
    }


}
