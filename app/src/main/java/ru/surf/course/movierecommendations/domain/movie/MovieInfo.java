package ru.surf.course.movierecommendations.domain.movie;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import ru.surf.course.movierecommendations.domain.Media;

@Data
public class MovieInfo extends Media implements Serializable {

    private final String LOG_TAG = getClass().getSimpleName();

    @SerializedName("revenue")
    private String revenue;

    @SerializedName("runtime")
    private int runtime;

    public MovieInfo(int id) {
        super(id);
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

    public static class RetrofitResult {

        @SerializedName("results")
        public List<MovieInfo> results;
    }
}
