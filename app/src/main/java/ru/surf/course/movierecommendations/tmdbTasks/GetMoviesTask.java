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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.MovieInfo;

/**
 * Created by andrew on 12/3/16.
 */




public class GetMoviesTask extends AsyncTask<String, Void, List<MovieInfo>> {

    private boolean isLoadingList;

    private final String API_KEY_PARAM = "api_key";
    private final String LANGUAGE_PARAM = "language";
    private final String PAGE_PARAM = "page";
    private final String NAME_PARAM = "query";

    public interface TaskCompletedListener {
        void taskCompleted(List<MovieInfo> result);
    }

    private final String LOG_TAG = getClass().getSimpleName();

    private List<TaskCompletedListener> listeners = new ArrayList<TaskCompletedListener>();

    public void addListener(TaskCompletedListener toAdd){
        listeners.add(toAdd);
    }

    private void invokeEvent(List<MovieInfo> result){
        for (TaskCompletedListener listener : listeners)
            listener.taskCompleted(result);
    }

    public void getMovieInfo(int movieId, String language) {
        isLoadingList = false;
        execute(Integer.toString(movieId), language);
    }

    public void getMovies(String language, String filter) {
        isLoadingList = true;
        execute(filter, language);
    }

    public void getMoviesByName(String name) {
        isLoadingList = true;
        execute(name);
    }

    private Uri uriByName(String name) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/search/movie?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(NAME_PARAM, name)
                .build();
    }

    private Uri uriByFilterOrId(String filter, String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + filter + "?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();
    }

    private boolean isFilterOrId(String toCheck) {
        //TODO заменить на константы или внешнее перечисление
        return toCheck.equalsIgnoreCase("popular")
                || toCheck.equalsIgnoreCase("top_rated")
                || toCheck.equalsIgnoreCase("now_playing")
                || toCheck.equalsIgnoreCase("upcoming")
                || isInteger(toCheck, 10);
    }

    private static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    @Override
    protected List<MovieInfo> doInBackground(String... params) {
        if (params.length == 0)
            return null;

        String language;
        if (params.length > 1)
            language = params[1];
        else language = "en";

        String page;
        if (params.length > 2)
            page = params[2];
        else page = "1";

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String movieJsonStr = null;

        try {

            Uri builtUri;
            if (isFilterOrId(params[0])) {
                builtUri = uriByFilterOrId(params[0], language, page);
            } else {
                builtUri = uriByName(params[0]);
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
            return result;
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;


    }

    @Override
    protected void onPostExecute(List<MovieInfo> movieInfos) {
        invokeEvent(movieInfos);
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
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_GENRE_IDS = "genre_ids";
        final String TMDB_GENRES ="genres";
        final String TMDB_NAME = "name";
        final String TMDB_PRODUCTION_COMPANIES = "production_companies";
        final String TMDB_PRODUCTION_COUNTRIES = "production_countries";
        final String TMDB_BUDGET = "budget";
        final String TMDB_REVENUE = "revenue";


        JSONObject movieJson = new JSONObject(jsonStr);


        List<MovieInfo> result = new ArrayList<>();
        MovieInfo item;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        if (isLoadingList){

            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);
            JSONArray genres;

            List<Integer> genresList;
            for (int i = 0; i < movieArray.length(); i++) {
                //get genres
                genres = movieArray.getJSONObject(i).getJSONArray(TMDB_GENRE_IDS);
                genresList = new ArrayList<>();
                for (int k = 0; k < genres.length(); k++)
                    genresList.add(genres.getInt(k));


                item = new MovieInfo(
                        movieArray.getJSONObject(i).getString(TMDB_TITLE),
                        movieArray.getJSONObject(i).getString(TMDB_ORIGINAL_TITLE),
                        genresList,
                        movieArray.getJSONObject(i).getString(TMDB_POSTER_PATH),
                        movieArray.getJSONObject(i).getString(TMDB_OVERVIEW),
                        formatter.parse(movieArray.getJSONObject(i).getString(TMDB_DATE)),
                        movieArray.getJSONObject(i).getString(TMDB_BACKDROP_PATH),
                        movieArray.getJSONObject(i).getDouble(TMDB_RATING),
                        movieArray.getJSONObject(i).getInt(TMDB_VOTE_COUNT),
                        movieArray.getJSONObject(i).getInt(TMDB_ID));
                result.add(item);
            }
        } else {
            //get genres
            JSONArray genres;
            List<Integer> genresListIds;
            List<String> genresListNames;
            genres = movieJson.getJSONArray(TMDB_GENRES);
            genresListIds = new ArrayList<>();
            genresListNames = new ArrayList<>();
            for (int k = 0; k < genres.length(); k++) {
                genresListIds.add(genres.getJSONObject(k).getInt(TMDB_ID));
                genresListNames.add(genres.getJSONObject(k).getString(TMDB_NAME));
            }

            //get production companies names
            JSONArray productionCompanies;
            List<String> productionCompaniesNames;
            productionCompanies = movieJson.getJSONArray(TMDB_PRODUCTION_COMPANIES);
            productionCompaniesNames = new ArrayList<>();
            for (int k = 0; k < productionCompanies.length(); k++)
                productionCompaniesNames.add(productionCompanies.getJSONObject(k).getString(TMDB_NAME));

            //get production countries
            JSONArray productionCountries;
            List<String> productionCountriesNames;
            productionCountries = movieJson.getJSONArray(TMDB_PRODUCTION_COUNTRIES);
            productionCountriesNames = new ArrayList<>();
            for (int k = 0; k < productionCountries.length(); k++)
                productionCountriesNames.add(productionCountries.getJSONObject(k).getString(TMDB_NAME));

            item = new MovieInfo(
                    movieJson.getString(TMDB_TITLE),
                    movieJson.getString(TMDB_ORIGINAL_TITLE),
                    genresListIds,
                    movieJson.getString(TMDB_POSTER_PATH),
                    movieJson.getString(TMDB_OVERVIEW),
                    formatter.parse(movieJson.getString(TMDB_DATE)),
                    movieJson.getString(TMDB_BACKDROP_PATH),
                    movieJson.getDouble(TMDB_RATING),
                    movieJson.getInt(TMDB_VOTE_COUNT),
                    movieJson.getInt(TMDB_ID),
                    movieJson.getString(TMDB_BUDGET),
                    genresListNames,
                    productionCompaniesNames,
                    productionCountriesNames,
                    movieJson.getString(TMDB_REVENUE));
            result.add(item);
        }

        return  result;
    }
}
