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
import java.util.List;

import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.models.People;

/**
 * Created by andrew on 1/29/17.
 */

public class GetPeopleTask extends AsyncTask<String, Void, List<People>> {

    private final String API_KEY_PARAM = "api_key";
    private final String LOG_TAG = getClass().getSimpleName();

    private Tasks task;
    private boolean isLoadingList;

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

        return null;
    }

    @Override
    protected void onPostExecute(List<People> peoples) {
        super.onPostExecute(peoples);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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


}
