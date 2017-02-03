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
import ru.surf.course.movierecommendations.models.Credit;
import ru.surf.course.movierecommendations.models.CrewMember;
import ru.surf.course.movierecommendations.models.Person;

/**
 * Created by andrew on 1/29/17.
 */

public class GetPersonsTask extends AsyncTask<String, Void, List<Person>> {

    private final String API_KEY_PARAM = "api_key";
    private final String TMDB_ID = "id";
    private final String TMDB_NAME = "name";
    private final String TMDB_PROFILE_PATH = "profile_path";

    private final String LOG_TAG = getClass().getSimpleName();

    private Tasks task;
    private boolean isLoadingList;
    private List<PersonsTaskCompleteListener> listeners;

    public GetPersonsTask() {
        listeners = new ArrayList<>();
    }

    @Override
    protected List<Person> doInBackground(String... strings) {

        if (strings.length == 0)
            return null;

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String personsJsonStr = null;

        try {

            Uri builtUri;
            switch (task) {
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
            personsJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Person list: " + personsJsonStr);
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
            List<Person> result = parseJson(personsJsonStr);
            return result;
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Person> person) {
        invokeCompleteEvent(person);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private List<Person> parseJson(String jsonStr) throws JSONException, ParseException {

        JSONObject jsonObject = new JSONObject(jsonStr);

        List<Person> result = new ArrayList<>();

        switch (task) {

        }

        return result;

    }

    public void addListener(PersonsTaskCompleteListener listener) {
        listeners.add(listener);
    }

    private void invokeCompleteEvent(List<Person> result) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).taskCompleted(result);
    }

    public interface PersonsTaskCompleteListener {
        void taskCompleted(List<Person> result);
    }


}
