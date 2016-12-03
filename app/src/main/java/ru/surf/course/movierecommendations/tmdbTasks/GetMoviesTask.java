package ru.surf.course.movierecommendations.tmdbTasks;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.MovieInfo;

/**
 * Created by andrew on 12/3/16.
 */

interface CompletedListener{
    void taskComleted();
}


public class GetMoviesTask extends AsyncTask<String, Void, List<MovieInfo>> {

    private final String LOG_TAG = getClass().getSimpleName();

    private List<CompletedListener> listeners = new ArrayList<CompletedListener>();

    public void addListener(CompletedListener toAdd){
        listeners.add(toAdd);
    }

    private void invokeEvent(){
        for (CompletedListener listener : listeners)
            listener.taskComleted();
    }

    @Override
    protected List<MovieInfo> doInBackground(String... params) {
        if (params.length == 0)
            return null;

        String language = "en";
        if (params.length > 1)
            language = params[1];

        String page = "1";
        if (params.length > 2)
            page = params[2];

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String movieJsonStr = null;

        try {
            final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "?";
            final String API_KEY_PARAM = "api_key";
            final String LANGUAGE_PARAM = "language";
            final String PAGE_PARAM = "page";

            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(PAGE_PARAM, page)
                    .build();

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
            movieJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Movie list: " + movieJsonStr);
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
            List<MovieInfo> result = parseJson(movieJsonStr);
            invokeEvent();
            return result;
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;


    }

    @Override
    protected void onPostExecute(List<MovieInfo> movieInfos) {
        super.onPostExecute(movieInfos);
    }

    private List<MovieInfo> parseJson(String jsonStr) throws JSONException, ParseException {
        final String TMDB_RESULTS = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_TITLE = "title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_DATE = "release_date";
        final String TMDB_BACKDROP_PATH = "backdrop_path";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_RATING = "vote_average";
        final String TMDB_ID = "id";

        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

        List<MovieInfo> result = new ArrayList<>();
        MovieInfo item;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        for (int i = 0; i < movieArray.length(); i++) {
            item = new MovieInfo(
                    movieArray.getJSONObject(i).getString(TMDB_TITLE),
                    movieArray.getJSONObject(i).getString(TMDB_POSTER_PATH),
                    movieArray.getJSONObject(i).getString(TMDB_OVERVIEW),
                    formatter.parse(movieArray.getJSONObject(i).getString(TMDB_DATE)),
                    movieArray.getJSONObject(i).getString(TMDB_BACKDROP_PATH),
                    movieArray.getJSONObject(i).getDouble(TMDB_RATING),
                    movieArray.getJSONObject(i).getInt(TMDB_VOTE_COUNT),
                    movieArray.getJSONObject(i).getInt(TMDB_ID));
            result.add(item);
        }

        return  result;
    }
}
