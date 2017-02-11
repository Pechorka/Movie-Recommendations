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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.models.MovieInfo;

/**
 * Created by andrew on 12/3/16.
 */


public class GetMoviesTask extends AsyncTask<String, Void, List<MovieInfo>> {

    private final String API_KEY_PARAM = "api_key";
    private final String LANGUAGE_PARAM = "language";
    private final String PAGE_PARAM = "page";
    private final String NAME_PARAM = "query";
    private final String RELEASE_DATE_GTE = "release_date.gte";
    private final String RELEASE_DATE_LTE = "release_date.lte";
    private final String WITH_GENRES = "with_genres";
    private final String LOG_TAG = getClass().getSimpleName();
    private boolean isLoadingList;
    private boolean newResult;
    private Tasks task;
    private List<TaskCompletedListener> listeners = new ArrayList<TaskCompletedListener>();

    @Override
    protected List<MovieInfo> doInBackground(String... params) {
        if (params.length == 0)
            return null;

        String languageName;
        if (params.length > 1)
            languageName = params[1];
        else languageName = "en";


        String page;
        if (params.length > 2)
            page = params[2];
        else page = "1";

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String movieJsonStr = null;

        try {

            Uri builtUri;
            params[0] = prepareForUri(params[0]);
            switch (task) {
                case SEARCH_BY_FILTER:
                    builtUri = uriByFilterOrId(params[0], languageName, page);
                    break;
                case SEARCH_BY_ID:
                    builtUri = uriByFilterOrId(params[0], languageName, page);
                    break;
                case SEARCH_BY_NAME:
                    builtUri = uriByName(params[0]);
                    break;
                case SEARCH_BY_GENRE:
                    builtUri = uriByGenre(Integer.parseInt(params[0]), languageName);
                    break;
                case SEARCH_SIMILAR:
                    builtUri = uriSimilar(params[0], languageName, page);
                    break;
                case SEARCH_BY_KEYWORD:
                    builtUri = uriByKeyword(params[0], languageName);
                    break;
                case SEARCH_BY_CUSTOM_FILTER:
                    builtUri = uriForCustomFilter(languageName, page, params[0], params[3], params[4]);
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
            List<MovieInfo> result = new ArrayList<>();
            if (movieJsonStr != null) {
                result = parseJson(movieJsonStr);
                fillInInfoLanguage(result, new Locale(languageName));
                newResult = Integer.parseInt(page) <= 1;
            }
            return result;
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;


    }

    private void fillInInfoLanguage(List<MovieInfo> list, Locale language) {
        for (MovieInfo movie : list) {
            movie.setInfoLanguage(language);
        }
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
        final String TMDB_ORIGINAL_LANGUAGE = "original_language";
        final String TMDB_GENRE_IDS = "genre_ids";
        final String TMDB_GENRES = "genres";
        final String TMDB_NAME = "name";
        final String TMDB_PRODUCTION_COMPANIES = "production_companies";
        final String TMDB_PRODUCTION_COUNTRIES = "production_countries";
        final String TMDB_BUDGET = "budget";
        final String TMDB_REVENUE = "revenue";
        final String TMDB_STATUS = "status";
        final String TMDB_RUNTIME = "runtime";


        JSONObject movieJson = new JSONObject(jsonStr);


        List<MovieInfo> result = new ArrayList<>();
        MovieInfo item;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        if (isLoadingList) {

            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);
            JSONArray genres;

            List<Integer> genresList;
            for (int i = 0; i < movieArray.length(); i++) {
                //get genres
                genres = movieArray.getJSONObject(i).getJSONArray(TMDB_GENRE_IDS);
                genresList = new ArrayList<>();
                for (int k = 0; k < genres.length(); k++)
                    genresList.add(genres.getInt(k));


                item = new MovieInfo(movieArray.getJSONObject(i).getInt(TMDB_ID));
                item.setTitle(movieArray.getJSONObject(i).getString(TMDB_TITLE));
                item.setOriginalTitle(movieArray.getJSONObject(i).getString(TMDB_ORIGINAL_TITLE));
                item.setGenreIds(genresList);
                item.setPosterPath(movieArray.getJSONObject(i).getString(TMDB_POSTER_PATH));
                item.setOverview(movieArray.getJSONObject(i).getString(TMDB_OVERVIEW));
                item.setBackdropPath(movieArray.getJSONObject(i).getString(TMDB_BACKDROP_PATH));
                item.setVoteAverage(movieArray.getJSONObject(i).getDouble(TMDB_RATING));
                item.setVoteCount(movieArray.getJSONObject(i).getInt(TMDB_VOTE_COUNT));

                try {
                    item.setDate(formatter.parse(movieArray.getJSONObject(i).getString(TMDB_DATE)));
                } catch (ParseException e) {
                    Log.d(LOG_TAG, "Empty date, most likely");
                }
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

            item = new MovieInfo(movieJson.getInt(TMDB_ID));
            item.setTitle(movieJson.getString(TMDB_TITLE));
            item.setOriginalTitle(movieJson.getString(TMDB_ORIGINAL_TITLE));
            item.setOriginalLanguage(new Locale(movieJson.getString(TMDB_ORIGINAL_LANGUAGE)));
            item.setGenreIds(genresListIds);
            item.setPosterPath(movieJson.getString(TMDB_POSTER_PATH));
            item.setOverview(movieJson.getString(TMDB_OVERVIEW));
            item.setBackdropPath(movieJson.getString(TMDB_BACKDROP_PATH));
            item.setVoteAverage(movieJson.getDouble(TMDB_RATING));
            item.setVoteCount(movieJson.getInt(TMDB_VOTE_COUNT));
            item.setBudget(movieJson.getString(TMDB_BUDGET));
            item.setGenreNames(genresListNames);
            item.setProductionCompaniesNames(productionCompaniesNames);
            item.setProductionCountriesNames(productionCountriesNames);
            item.setRevenue(movieJson.getString(TMDB_REVENUE));
            item.setStatus(movieJson.getString(TMDB_STATUS));
            item.setRuntime(movieJson.getInt(TMDB_RUNTIME));
            try {
                item.setDate(formatter.parse(movieJson.getString(TMDB_DATE)));
            } catch (ParseException e) {
                Log.d(LOG_TAG, "Empty date, most likely");
            }
            result.add(item);
        }

        return result;
    }

    public void addListener(TaskCompletedListener toAdd) {
        listeners.add(toAdd);
    }

    private void invokeEvent(List<MovieInfo> result) {
        for (TaskCompletedListener listener : listeners)
            listener.moviesLoaded(result, newResult);
    }

    public void getMovieById(int movieId, String language) {
        isLoadingList = false;
        task = Tasks.SEARCH_BY_ID;
        execute(Integer.toString(movieId), language);
    }

    public void getMoviesByFilter(String filter, String language, String page) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_FILTER;
        execute(filter, language, page);
    }

    public void getMoviesByName(String name) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_NAME;
        execute(name);
    }

    public void getMoviesByGenre(int genreId, String language) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_GENRE;
        execute(Integer.toString(genreId), language);
    }

    public void getSimilarMovies(int movieId, String language) {
        isLoadingList = true;
        task = Tasks.SEARCH_SIMILAR;
        execute(Integer.toString(movieId), language);
    }

    public void getMoviesByKeyword(int keywordId, String language) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_KEYWORD;
        execute(Integer.toString(keywordId), language);
    }

    public void getMoviesByCustomFilter(String language, String page, String genres,
                                        String releaseDateGTE, String releaseDateLTE) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_CUSTOM_FILTER;
        execute(genres, language, page, releaseDateLTE, releaseDateGTE);

    }

    private String prepareForUri(String filter) {
        return filter.toLowerCase().replace(' ', '_');
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


    private Uri uriForCustomFilter(String language, String page,
                                   String genres, String releaseDateGTE, String releaseDateLTE) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        Uri.Builder uri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(RELEASE_DATE_GTE, releaseDateGTE)
                .appendQueryParameter(RELEASE_DATE_LTE, releaseDateLTE);
        if (genres.length() != 0) {
            uri.appendQueryParameter(WITH_GENRES, genres);
        }
        return uri.build();
    }

    private Uri uriByGenre(int genreID, String language) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/genre/" + genreID + "?/movies";
        //TODO add sort
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .build();
    }

    private Uri uriSimilar(String movieId, String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + movieId + "/similar?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();
    }

    private Uri uriByKeyword(String keywordID, String language) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/keyword/" + keywordID + "/movies?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .build();
    }


    public interface TaskCompletedListener {
        void moviesLoaded(List<MovieInfo> result, boolean newResult);
    }
}
