package ru.surf.course.movierecommendations.interactor.common.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;


@Data
public class BaseResponse {
    @SerializedName("error")
    private List<String> error;

}
