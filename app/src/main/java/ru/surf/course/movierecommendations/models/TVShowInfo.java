package ru.surf.course.movierecommendations.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sergey on 07.02.2017.
 */

public class TVShowInfo extends Media implements Serializable {

    private List<Double> mEpisodesRuntime;
    private int mNumberOfEpisodes;
    private int mNumberOfSeasons;
    private String mType;
    private List<String> originCountryList;

    public TVShowInfo(int id) {
        super(id);
    }

    public List<Double> getEpisodesRuntime() {
        return mEpisodesRuntime;
    }

    public void setEpisodesRuntime(List<Double> episodesRuntime) {
        this.mEpisodesRuntime = episodesRuntime;
    }

    public int getNumberOfEpisodes() {
        return mNumberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.mNumberOfEpisodes = numberOfEpisodes;
    }

    public int getNumberOfSeasons() {
        return mNumberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.mNumberOfSeasons = numberOfSeasons;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public List<String> getOriginCountryList() {
        return originCountryList;
    }

    public void setOriginCountryList(List<String> originCountryList) {
        this.originCountryList = originCountryList;
    }
}
