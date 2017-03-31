package ru.surf.course.movierecommendations.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sergey on 07.02.2017.
 */

public class TVShowInfo extends Media implements Serializable {

    private final String LOG_TAG = getClass().getSimpleName();

    @SerializedName("episode_run_time")
    private List<Double> mEpisodesRuntime;
    @SerializedName("number_of_episodes")
    private int mNumberOfEpisodes;
    @SerializedName("number_of_seasons")
    private int mNumberOfSeasons;
    @SerializedName("type")
    private String mType;
    @SerializedName("origin_country")
    private List<String> originCountryList;
    @SerializedName("seasons")
    private List<Season> seasonList;
    @SerializedName("homepage")
    private String homePage;
    @SerializedName("networks")
    private List<Network> networks;

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

    public List<Season> getSeasonList() {
        return seasonList;
    }

    public void setSeasonList(List<Season> seasonList) {
        this.seasonList = seasonList;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }

    public void fillFields(Object from) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(from.getClass().getDeclaredFields()));
        fields.addAll(Arrays.asList(from.getClass().getSuperclass().getDeclaredFields()));
        for (Field field : fields) {
            Field fieldFrom = null;
            Field fieldTo = null;
            Object value = null;
            try {
                fieldFrom = from.getClass().getDeclaredField(field.getName());
            } catch (NoSuchFieldException e) {
                try {
                    fieldFrom = from.getClass().getSuperclass().getDeclaredField(field.getName());
                } catch (NoSuchFieldException secondE) {
                    Log.d(LOG_TAG, "Copy error " + e.getMessage());
                }
            }
            try {
                fieldTo = this.getClass().getDeclaredField(field.getName());
            } catch (NoSuchFieldException e) {
                try {
                    fieldTo = this.getClass().getSuperclass().getDeclaredField(field.getName());
                } catch (NoSuchFieldException secondE) {
                    Log.d(LOG_TAG, "Copy error " + e.getMessage());
                }
            }
            try {
                value = fieldFrom.get(from);
                fieldTo.set(this, value);
            } catch (IllegalAccessException e) {
                Log.d(LOG_TAG, "Copy error " + e.getMessage());
            }
        }
    }
}
