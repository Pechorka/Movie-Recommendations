package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.domain.Review;

/**
 * Created by andrew on 2/18/17.
 */

public class GetReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {

  private final String API_KEY_PARAM = "api_key";
  private final String TMDB_RESULTS = "results";
  private final String TMDB_ID = "id";
  private final String TMDB_AUTHOR = "author";
  private final String TMDB_CONTENT = "content";
  private final String TMDB_URL = "url";
  private final String LOG_TAG = getClass().getSimpleName();

  private Tasks task;
  private boolean isLoadingList;
  private List<ReviewsTaskCompleteListener> listeners;

  public GetReviewsTask() {
    listeners = new ArrayList<>();
  }

  @Override
  protected ArrayList<Review> doInBackground(String... strings) {

    if (strings.length == 0) {
      return null;
    }

    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;

    String creditsJsonStr = null;

    try {

      Uri builtUri;
      switch (task) {
        case GET_MOVIE_REVIEWS:
          builtUri = uriByMovieId(Integer.valueOf(strings[0]));
          break;
        default:
          builtUri = Uri.EMPTY;
      }

      Log.d(LOG_TAG, "Built uri" + builtUri);

      URL url = new URL(builtUri.toString());

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
      creditsJsonStr = buffer.toString();

      Log.v(LOG_TAG, "Reviews list: " + creditsJsonStr);
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
      return parseJson(creditsJsonStr);
    } catch (JSONException | ParseException e) {
      Log.e(LOG_TAG, e.getMessage(), e);
      e.printStackTrace();
    }

    return null;
  }

  @Override
  protected void onPostExecute(ArrayList<Review> reviews) {
    invokeCompleteEvent(reviews);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
  }

  private ArrayList<Review> parseJson(String jsonStr) throws JSONException, ParseException {

    JSONObject jsonObject = new JSONObject(jsonStr);
    JSONArray results = jsonObject.getJSONArray(TMDB_RESULTS);

    ArrayList<Review> result = new ArrayList<>();

    switch (task) {
      case GET_MOVIE_REVIEWS:
        for (int i = 0; i < results.length(); i++) {
          result.add(parseMovieReview(results.getJSONObject(i)));
        }
        break;
    }

    return result;

  }

  private Review parseMovieReview(JSONObject jsonObject) throws JSONException {
    Review review = new Review(jsonObject.getString(TMDB_ID));
    review.setAuthor(jsonObject.getString(TMDB_AUTHOR));
    review.setContent(jsonObject.getString(TMDB_CONTENT));
    try {
      review.setURL(new URL(jsonObject.getString(TMDB_URL)));
    } catch (MalformedURLException e) {
      Log.d(LOG_TAG, "Url error", e);
    }
    return review;
  }

  public void getMovieReviews(int movieId) {
    isLoadingList = true;
    task = Tasks.GET_MOVIE_REVIEWS;
    execute(String.valueOf(movieId));
  }


  private Uri uriByMovieId(int movieId) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    final String TMDB_REVIEWS = "reviews";
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendPath(String.valueOf(movieId))
        .appendPath(TMDB_REVIEWS)
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .build();
  }

  public void addListener(ReviewsTaskCompleteListener listener) {
    listeners.add(listener);
  }

  private void invokeCompleteEvent(ArrayList<Review> result) {
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).taskCompleted(result);
    }
  }

  public interface ReviewsTaskCompleteListener {

    void taskCompleted(ArrayList<Review> result);
  }

}
