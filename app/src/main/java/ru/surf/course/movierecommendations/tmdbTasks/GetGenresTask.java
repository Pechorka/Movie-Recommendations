package ru.surf.course.movierecommendations.tmdbTasks;

import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.surf.course.movierecommendations.BuildConfig;

/**
 * Created by Sergey on 06.02.2017.
 */

public class GetGenresTask extends AsyncTask<Tasks, Void, Map<String, Integer>> {

    private final String API_KEY_PARAM = "api_key";

    private final String LOG_TAG = getClass().getSimpleName();

    private List<GetGenresTask.TaskCompletedListener> listeners = new ArrayList<GetGenresTask.TaskCompletedListener>();

    @Override
    protected Map<String, Integer> doInBackground(Tasks... params) {
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
    protected void onPostExecute(Map<String, Integer> movieInfos) {
        invokeEvent(movieInfos);
    }

    private void invokeEvent(Map<String, Integer> result) {
        for (GetGenresTask.TaskCompletedListener listener : listeners)
            listener.taskCompleted(result);
    }

    public void addListener(GetGenresTask.TaskCompletedListener toAdd) {
        listeners.add(toAdd);
    }

    public void getGenres(Tasks task) {
        execute(task);
    }

    private Map<String, Integer> parseJson(String jsonStr) throws JSONException {
        final String TMDB_GENRES = "genres";
        final String TMDB_ID = "id";
        final String TMDB_NAME = "name";

        JSONObject genreJson = new JSONObject(jsonStr);
        HashMap<String, Integer> result = new HashMap<>();
        JSONArray genresArray = genreJson.getJSONArray(TMDB_GENRES);
        for (int i = 0; i < genresArray.length(); i++) {
            result.put(genresArray.getJSONObject(i).getString(TMDB_NAME)
                    , genresArray.getJSONObject(i).getInt(TMDB_ID));
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
        void taskCompleted(Map<String, Integer> result);
    }

}
