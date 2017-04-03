package ru.surf.course.movierecommendations.tmdbTasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.models.Genre;

/**
 * Created by Sergey on 06.02.2017.
 */

public class GetGenresTask extends AsyncTask<Tasks, Void, List<Genre>> {

  private final String API_KEY_PARAM = "api_key";

  private final String LOG_TAG = getClass().getSimpleName();

  private List<GetGenresTask.TaskCompletedListener> listeners = new ArrayList<>();

  @Override
  protected List<Genre> doInBackground(Tasks... params) {
    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;

    String genresJsonStr = null;

    try {
      Uri builtUri;
      switch (params[0]) {
        case GET_TV_GENRES:
          builtUri = uriForGenres("tv");
          break;
        case GET_MOVIE_GENRES:
          builtUri = uriForGenres("movie");
          break;
        default:
          builtUri = Uri.EMPTY;
      }
      URL url = new URL(builtUri.toString());

      Log.v(LOG_TAG, "Built URI " + builtUri.toString());

      httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("GET");
      httpURLConnection.connect();

      InputStream inputStream = httpURLConnection.getInputStream();
      StringBuilder buffer = new StringBuilder();
      if (inputStream == null) {
        return null;
      }
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        buffer.append(line).append("\n");
      }
      if (buffer.length() == 0) {
        return null;
      }

      genresJsonStr = buffer.toString();
      Log.v(LOG_TAG, "Genres list: " + genresJsonStr);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Error", e);
    } finally {
      if (httpURLConnection != null) {
        httpURLConnection.disconnect();
      }
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (final IOException e) {
          Log.e(LOG_TAG, "Error closing stream", e);
        }
      }
    }
    try {
      return parseJson(genresJsonStr);
    } catch (JSONException e) {
      Log.e(LOG_TAG, e.getMessage(), e);
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(List<Genre> genres) {
    invokeEvent(genres);
  }

  private void invokeEvent(List<Genre> genres) {
    for (GetGenresTask.TaskCompletedListener listener : listeners) {
      listener.taskCompleted(genres);
    }
  }

  public void addListener(GetGenresTask.TaskCompletedListener toAdd) {
    listeners.add(toAdd);
  }

  public void getGenres(Tasks task) {
    execute(task);
  }


  private List<Genre> parseJson(String jsonStr) throws JSONException {
    final String TMDB_GENRES = "genres";
    final String TMDB_ID = "id";
    final String TMDB_NAME = "name";

    JSONObject genreJson = new JSONObject(jsonStr);
    List<Genre> result = new ArrayList<>();
    JSONArray genresArray = genreJson.getJSONArray(TMDB_GENRES);
    Genre genre;
    for (int i = 0; i < genresArray.length(); i++) {
      genre = new Genre(genresArray.getJSONObject(i).getInt(TMDB_ID),
          genresArray.getJSONObject(i).getString(TMDB_NAME));
      result.add(genre);
    }
    return result;
  }


  private Uri uriForGenres(String whose) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/genre/" + whose + "/list?";
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .build();
  }


  public interface TaskCompletedListener {

    void taskCompleted(List<Genre> genres);
  }

}
