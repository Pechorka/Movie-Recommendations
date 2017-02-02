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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.models.Actor;
import ru.surf.course.movierecommendations.models.CrewMember;
import ru.surf.course.movierecommendations.models.People;

/**
 * Created by andrew on 1/29/17.
 */

public class GetPeopleTask extends AsyncTask<String, Void, List<People>> {

    private final String API_KEY_PARAM = "api_key";
    private final String TMDB_CAST = "cast";
    private final String TMDB_CREW = "crew";
    private final String TMDB_CAST_ID = "cast_id";
    private final String TMDB_CHARACTER = "character";
    private final String TMDB_CREDIT_ID = "credit_id";
    private final String TMDB_ID = "id";
    private final String TMDB_NAME = "name";
    private final String TMDB_ORDER = "order";
    private final String TMDB_PROFILE_PATH = "profile_path";
    private final String TMDB_DEPARTMENT = "department";
    private final String TMDB_JOB = "job";
    private final String LOG_TAG = getClass().getSimpleName();

    private Tasks task;
    private boolean isLoadingList;
    private List<PeopleTaskCompleteListener> listeners;

    public GetPeopleTask() {
        listeners = new ArrayList<>();
    }

    @Override
    protected List<People> doInBackground(String... strings) {

        if (strings.length == 0)
            return null;

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String peopleJsonStr = null;

        try {

            Uri builtUri;
            switch (task) {
                case GET_MOVIE_CREDITS:
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
            peopleJsonStr = buffer.toString();

            Log.v(LOG_TAG, "People list: " + peopleJsonStr);
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
            List<People> result = parseJson(peopleJsonStr);
            return result;
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<People> people) {
        invokeCompleteEvent(people);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private List<People> parseJson(String jsonStr) throws JSONException, ParseException {

        JSONObject jsonObject = new JSONObject(jsonStr);

        List<People> result = new ArrayList<>();

        switch (task) {
            case GET_MOVIE_CREDITS:
                result = parseMovieCredits(jsonObject);
                break;
        }

        return result;

    }

    public void getMovieCredits(int movieId) {
        isLoadingList = true;
        task = Tasks.GET_MOVIE_CREDITS;
        execute(String.valueOf(movieId));
    }

    private Uri uriByMovieId(int movieId) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie";
        final String TMDB_CREDITS = "credits";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(TMDB_CREDITS)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .build();
    }

    private List<People> parseMovieCredits(JSONObject jsonObject) throws JSONException {
        JSONArray cast = jsonObject.getJSONArray(TMDB_CAST);
        JSONArray crew = jsonObject.getJSONArray(TMDB_CREW);
        List<People> result = new ArrayList<>();
        People people;
        for (int i = 0; i < cast.length(); i++) {
            people = new Actor(
                    cast.getJSONObject(i).getString(TMDB_NAME),
                    cast.getJSONObject(i).getInt(TMDB_ID),
                    cast.getJSONObject(i).getString(TMDB_PROFILE_PATH),
                    cast.getJSONObject(i).getInt(TMDB_CAST_ID),
                    cast.getJSONObject(i).getString(TMDB_CHARACTER),
                    cast.getJSONObject(i).getString(TMDB_CREDIT_ID),
                    cast.getJSONObject(i).getInt(TMDB_ORDER)
            );
            result.add(people);
        }

        for (int i = 0; i < crew.length(); i++) {
            people = new CrewMember(
                    crew.getJSONObject(i).getString(TMDB_NAME),
                    crew.getJSONObject(i).getInt(TMDB_ID),
                    crew.getJSONObject(i).getString(TMDB_PROFILE_PATH),
                    crew.getJSONObject(i).getString(TMDB_CREDIT_ID),
                    crew.getJSONObject(i).getString(TMDB_DEPARTMENT),
                    crew.getJSONObject(i).getString(TMDB_JOB)
            );
            result.add(people);
        }

        return result;
    }

    public void addListener(PeopleTaskCompleteListener listener) {
        listeners.add(listener);
    }

    private void invokeCompleteEvent(List<People> result) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).taskCompleted(result);
    }

    public interface PeopleTaskCompleteListener {
        void taskCompleted(List<People> result);
    }


}
