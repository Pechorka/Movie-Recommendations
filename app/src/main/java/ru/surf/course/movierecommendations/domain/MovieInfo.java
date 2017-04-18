package ru.surf.course.movierecommendations.domain;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieInfo extends Media implements Serializable {

  private final String LOG_TAG = getClass().getSimpleName();

  @SerializedName("revenue")
  private String mRevenue;

  @SerializedName("runtime")
  private int mRuntime;

  public MovieInfo(int id) {
    super(id);
  }

  public static List<MovieInfo> createMovieInfoList(int[] imageIDs, String[] names) {
    if (imageIDs.length != names.length) {
      throw new IllegalArgumentException("Length of arrays should be same");
    }
    List<MovieInfo> movieInfoList = new ArrayList<>();
    MovieInfo movieInfo;
    for (int i = 0; i < imageIDs.length; i++) {
      movieInfo = new MovieInfo(imageIDs[i]);
      movieInfo.mTitle = names[i];
      movieInfoList.add(movieInfo);
    }
    return movieInfoList;
  }

  public String getRevenue() {
    return mRevenue;
  }

  public void setRevenue(String revenue) {
    this.mRevenue = revenue;
  }

  public int getRuntime() {
    return mRuntime;
  }

  public void setRuntime(int runtime) {
    this.mRuntime = runtime;
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
