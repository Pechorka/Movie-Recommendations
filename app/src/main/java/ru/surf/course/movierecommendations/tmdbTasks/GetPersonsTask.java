package ru.surf.course.movierecommendations.tmdbTasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.models.Person;

/**
 * Created by andrew on 1/29/17.
 */

public class GetPersonsTask extends AsyncTask<String, Void, List<Person>> {

    private final String API_KEY_PARAM = "api_key";
    private final String LANGUAGE_PARAM = "language";
    private final String TMDB_ID = "id";
    private final String TMDB_NAME = "name";
    private final String TMDB_PROFILE_PATH = "profile_path";
    private final String TMDB_ADULT = "adult";
    private final String TMDB_BIOGRAPHY = "biography";
    private final String TMDB_BIRTHDAY = "birthday";
    private final String TMDB_DEATHDAY = "deathday";
    private final String TMDB_GENDER = "gender";
    private final String TMDB_IMDB_ID = "imdb_id";
    private final String TMDB_PLACE_OF_BIRTH = "place_of_birth";
    private final String TMDB_POPULARITY = "popularity";

    private final String LOG_TAG = getClass().getSimpleName();

    private Tasks task;
    private boolean isLoadingList;
    private List<PersonsTaskCompleteListener> listeners;
    private Locale language;

    public GetPersonsTask() {
        listeners = new ArrayList<>();
        language = Locale.getDefault();
    }

    @Override
    protected List<Person> doInBackground(String... strings) {

        if (strings.length == 0)
            return null;

        if (strings.length > 1) {
            language = new Locale(strings[1]);
        }

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String personsJsonStr = null;

        try {

            Uri builtUri;
            switch (task) {
                case GET_PERSON_BY_ID:
                    builtUri = uriForPersonDetails(Integer.valueOf(strings[0]));
                    break;
                default:
                    builtUri = Uri.EMPTY;
            }

            Log.d(LOG_TAG, "Built uri " + builtUri);

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
            Log.e(LOG_TAG, "Error ", e);
            invokeErrorEvent(e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }

        try {
            List<Person> result = new ArrayList<>();
            if (personsJsonStr != null)
                parseJson(personsJsonStr);
            return result;
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            invokeErrorEvent(e);
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
            case GET_PERSON_BY_ID:
                result.add(parsePersonJson(jsonObject));
                break;
        }

        return result;

    }

    private Person parsePersonJson(JSONObject jsonObject) throws JSONException, ParseException {
        Person result;
        String temp;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        result = new Person(
                jsonObject.getString(TMDB_NAME),
                jsonObject.getInt(TMDB_ID)
        );

        result.setAdult(jsonObject.getBoolean(TMDB_ADULT));

        temp = jsonObject.getString(TMDB_BIOGRAPHY);
        if (Utilities.checkString(temp))
            result.setBiography(temp);

        temp = jsonObject.getString(TMDB_BIRTHDAY);
        if (Utilities.checkString(temp))
            result.setBirthday(simpleDateFormat.parse(temp));

        temp = jsonObject.getString(TMDB_DEATHDAY);
        if (Utilities.checkString(temp))
            result.setDeathday(simpleDateFormat.parse(temp));

        temp = jsonObject.getString(TMDB_IMDB_ID);
        if (Utilities.checkString(temp))
            result.setImdbId(temp);

        temp = jsonObject.getString(TMDB_PLACE_OF_BIRTH);
        if (Utilities.checkString(temp))
            result.setPlaceOfBirth(temp);

        temp = jsonObject.getString(TMDB_PROFILE_PATH);
        if (Utilities.checkString(temp))
            result.setProfilePath(temp);

        result.setGender(Person.Gender.values()[jsonObject.getInt(TMDB_GENDER)]);

        result.setPopularity(jsonObject.getDouble(TMDB_POPULARITY));

        result.setInfoLanguage(language);

        return result;
    }

    public void getPersonById(int personId, Locale language) {
        task = Tasks.GET_PERSON_BY_ID;
        isLoadingList = false;
        execute(String.valueOf(personId), language.getLanguage());
    }

    public void getPersonById(int personId) {
        getPersonById(personId, Locale.getDefault());
    }

    private Uri uriForPersonDetails(int personId) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/person";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(String.valueOf(personId))
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language.getLanguage())
                .build();
    }

    public void addListener(PersonsTaskCompleteListener listener) {
        listeners.add(listener);
    }

    private void invokeCompleteEvent(List<Person> result) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).taskCompleted(result);
    }

    private void invokeErrorEvent(Exception e) {
        for (PersonsTaskCompleteListener listener : listeners)
            listener.error(e);
    }

    public interface PersonsTaskCompleteListener {
        void taskCompleted(List<Person> result);
        void error(Exception e);
    }


}
