package ru.surf.course.movierecommendations.tmdbTasks;

import android.net.Uri;
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
import ru.surf.course.movierecommendations.models.Genre;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.models.Network;
import ru.surf.course.movierecommendations.models.Season;
import ru.surf.course.movierecommendations.models.TVShowInfo;

/**
 * Created by Sergey on 07.02.2017.
 */

public class GetTVShowsTask extends GetMediaTask {
    protected final String LOG_TAG = getClass().getSimpleName();

    @Override
    protected List<TVShowInfo> doInBackground(String... params) {
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

        String tShowJsonStr = null;

        try {

            Uri builtUri;
            switch (task) {
                case SEARCH_BY_FILTER:
                    builtUri = uriByFilterOrId(params[0], languageName, page);
                    break;
                case SEARCH_BY_ID:
                    builtUri = uriByFilterOrId(params[0], languageName, page);
                    break;
                case SEARCH_BY_NAME:
                    builtUri = uriByName(params[0], languageName, page);
                    break;
                case SEARCH_BY_GENRE:
                    builtUri = uriByGenres(params[0], languageName, page);
                    break;
                case SEARCH_SIMILAR:
                    builtUri = uriForSimilar(params[0], languageName, page);
                    break;
                case SEARCH_BY_KEYWORD:
                    builtUri = uriByKeywords(params[0], languageName, page);
                    break;
                case SEARCH_BY_CUSTOM_FILTER:
                    builtUri = uriForCustomFilter(languageName, page, params[0], params[3], params[4], params[5]);
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
            tShowJsonStr = buffer.toString();

            Log.v(LOG_TAG, "TVShow list: " + tShowJsonStr);
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
            newResult = Integer.parseInt(page) == 1;
            return parseJson(tShowJsonStr);
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<? extends Media> medias) {
        invokeEvent(medias);
    }


    @Override
    public void getMediaById(int movieId, String language) {
        isLoadingList = false;
        task = Tasks.SEARCH_BY_ID;
        execute(Integer.toString(movieId), language);
    }

    @Override
    public void getMediaByFilter(String filter, String language, String page) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_FILTER;
        execute(filter, language, page);
    }

    @Override
    public void getMediaByName(String name, String language, String page) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_NAME;
        execute(name, language, page);
    }

    @Override
    public void getMediaByGenre(String genreIds, String language, String page) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_GENRE;
        execute(genreIds, language, page);
    }

    @Override
    public void getSimilarMedia(int tvId, String language, String page) {
        isLoadingList = true;
        task = Tasks.SEARCH_SIMILAR;
        execute(Integer.toString(tvId), language, page);
    }

    @Override
    public void getMediaByKeywords(String keywordIds, String language, String page) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_KEYWORD;
        execute(keywordIds, language, page);
    }

    @Override
    public void getMediaByCustomFilter(String language, String page, String genres,
                                       String releaseDateGTE, String releaseDateLTE,
                                       String sortBy) {
        isLoadingList = true;
        task = Tasks.SEARCH_BY_CUSTOM_FILTER;
        execute(genres, language, page, releaseDateLTE, releaseDateGTE, sortBy);

    }


    private List<TVShowInfo> parseJson(String jsonStr) throws JSONException, ParseException {
        final String TMDB_RESULTS = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_NAME = "name";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_DATE = "release_date";
        final String TMDB_BACKDROP_PATH = "backdrop_path";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_RATING = "vote_average";
        final String TMDB_ID = "id";
        final String TMDB_ORIGINAL_NAME = "original_name";
        final String TMDB_ORIGINAL_LANGUAGE = "original_language";
        final String TMDB_GENRE_IDS = "genre_ids";
        final String TMDB_GENRES = "genres";
        final String TMDB_PRODUCTION_COMPANIES = "production_companies";
        final String TMDB_PRODUCTION_COUNTRIES = "production_countries";
        final String TMDB_BUDGET = "budget";
        final String TMDB_REVENUE = "revenue";
        final String TMDB_STATUS = "status";
        final String TMDB_RUNTIME = "runtime";
        final String TMDB_EPICSODE_RUNTIME = "episode_run_time";
        final String TMDB_NUMBER_OF_EPISODES = "number_of_episodes";
        final String TMDB_NUMBER_OF_SEASONS = "number_of_seasons";
        final String TMDB_TYPE = "type";
        final String TMDB_FIRST_AIR_DATE = "first_air_date";
        final String TMDB_ORIGIN_COUNTRY = "origin_country";
        final String TMDB_HOMEPAGE = "homepage";
        final String TMDB_NETWORKS = "networks";
        final String TMDB_SEASONS = "seasons";
        final String TMDB_EPISODE_COUNT = "episode_count";
        final String TMDB_SEASON_NUMBER = "season_number";
        final String TMDB_AIR_DATE = "air_date";


        JSONObject tvShowsJson = new JSONObject(jsonStr);


        List<TVShowInfo> result = new ArrayList<>();
        TVShowInfo item;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        if (isLoadingList) {

            JSONArray tvShowArray = tvShowsJson.getJSONArray(TMDB_RESULTS);
            JSONArray genres;
            JSONArray originCountries;

            List<Genre> genresList;
            List<String> originCountryList;

            for (int i = 0; i < tvShowArray.length(); i++) {
                //get genres
                genres = tvShowArray.getJSONObject(i).getJSONArray(TMDB_GENRE_IDS);
                genresList = new ArrayList<>();
                for (int k = 0; k < genres.length(); k++) {
                    genresList.add(new Genre(genres.getInt(k)));
                }
                //get originCountries
                originCountries = tvShowArray.getJSONObject(i).getJSONArray(TMDB_ORIGIN_COUNTRY);
                originCountryList = new ArrayList<>();
                for (int k = 0; k < originCountries.length(); k++) {
                    originCountryList.add(originCountries.getString(k));
                }


                item = new TVShowInfo(tvShowArray.getJSONObject(i).getInt(TMDB_ID));
                item.setTitle(tvShowArray.getJSONObject(i).getString(TMDB_NAME));
                item.setOriginalTitle(tvShowArray.getJSONObject(i).getString(TMDB_ORIGINAL_NAME));
                item.setGenres(genresList);
                item.setOriginCountryList(originCountryList);
                item.setPosterPath(tvShowArray.getJSONObject(i).getString(TMDB_POSTER_PATH));
                item.setOverview(tvShowArray.getJSONObject(i).getString(TMDB_OVERVIEW));
                item.setBackdropPath(tvShowArray.getJSONObject(i).getString(TMDB_BACKDROP_PATH));
                item.setVoteCount(tvShowArray.getJSONObject(i).getInt(TMDB_VOTE_COUNT));
                item.setVoteAverage(tvShowArray.getJSONObject(i).getDouble(TMDB_RATING));
                item.setOriginalLanguage(new Locale(tvShowArray.getJSONObject(i).getString(TMDB_ORIGINAL_LANGUAGE)));
                try {
                    item.setDate(formatter.parse(tvShowArray.getJSONObject(i).getString(TMDB_FIRST_AIR_DATE)));
                } catch (ParseException e) {
                    Log.d(LOG_TAG, "Empty date, most likely");
                }
                result.add(item);
            }
        } else {
            //get genres
            JSONArray genres = tvShowsJson.getJSONArray(TMDB_GENRES);
            List<Genre> genreList = new ArrayList<>();
            for (int k = 0; k < genres.length(); k++) {
                genreList.add(new Genre(
                        genres.getJSONObject(k).getInt(TMDB_ID),
                        genres.getJSONObject(k).getString(TMDB_NAME)
                ));
            }

            //get runtime of episodes
            JSONArray episodesRuntime = tvShowsJson.getJSONArray(TMDB_EPICSODE_RUNTIME);
            List<Double> episodesRuntimeList = new ArrayList<>();
            for (int k = 0; k < episodesRuntime.length(); k++) {
                episodesRuntimeList.add(episodesRuntime.getDouble(k));
            }

            //get production companies names
            JSONArray productionCompanies = tvShowsJson.getJSONArray(TMDB_PRODUCTION_COMPANIES);
            List<String> productionCompaniesNames = new ArrayList<>();
            for (int k = 0; k < productionCompanies.length(); k++)
                productionCompaniesNames.add(productionCompanies.getJSONObject(k).getString(TMDB_NAME));

            //get production countries
            List<String> productionCountriesNames = new ArrayList<>();
            try {
                JSONArray productionCountries = tvShowsJson.getJSONArray(TMDB_PRODUCTION_COUNTRIES);
                for (int k = 0; k < productionCountries.length(); k++)
                    productionCountriesNames.add(productionCountries.getJSONObject(k).getString(TMDB_NAME));
            } catch (JSONException e) {
                Log.d(LOG_TAG, "No production countries ", e);
            }

            //get networks
            JSONArray netmworks = tvShowsJson.getJSONArray(TMDB_NETWORKS);
            List<Network> networksList = new ArrayList<>();
            Network network;
            for (int k = 0; k < netmworks.length(); k++) {
                network = new Network();
                network.setId(netmworks.getJSONObject(k).getInt(TMDB_ID));
                network.setName(netmworks.getJSONObject(k).getString(TMDB_NAME));
                networksList.add(network);
            }

            //get originCountries
            JSONArray originCountries = tvShowsJson.getJSONArray(TMDB_ORIGIN_COUNTRY);
            List<String> originCountryList = new ArrayList<>();
            for (int k = 0; k < originCountries.length(); k++) {
                originCountryList.add(originCountries.getString(k));
            }

            JSONArray seasons = tvShowsJson.getJSONArray(TMDB_SEASONS);
            List<Season> seasonList = new ArrayList<>();
            Season season;
            for (int k = 0; k < seasons.length(); k++) {
                season = new Season();
                season.setId(seasons.getJSONObject(k).getInt(TMDB_ID));
                season.setEpisodeCount(seasons.getJSONObject(k).getInt(TMDB_EPISODE_COUNT));
                season.setPosterPath(seasons.getJSONObject(k).getString(TMDB_POSTER_PATH));
                season.setSeasonNumber(seasons.getJSONObject(k).getInt(TMDB_SEASON_NUMBER));
                try {
                    season.setAirDate(formatter.parse(tvShowsJson.getString(TMDB_AIR_DATE)));
                } catch (ParseException | JSONException e) {
                    Log.d(LOG_TAG, "Empty date in season, most likely");
                }
            }

            item = new TVShowInfo(tvShowsJson.getInt(TMDB_ID));
            item.setTitle(tvShowsJson.getString(TMDB_NAME));
            item.setOriginalTitle(tvShowsJson.getString(TMDB_ORIGINAL_NAME));
            item.setGenres(genreList);
            item.setPosterPath(tvShowsJson.getString(TMDB_POSTER_PATH));
            item.setOverview(tvShowsJson.getString(TMDB_OVERVIEW));
            item.setBackdropPath(tvShowsJson.getString(TMDB_BACKDROP_PATH));
            item.setVoteCount(tvShowsJson.getInt(TMDB_VOTE_COUNT));
            item.setVoteAverage(tvShowsJson.getDouble(TMDB_RATING));
            item.setOriginalLanguage(new Locale(tvShowsJson.getString(TMDB_ORIGINAL_LANGUAGE)));
            item.setEpisodesRuntime(episodesRuntimeList);
            item.setHomePage(tvShowsJson.getString(TMDB_HOMEPAGE));
            item.setNumberOfEpisodes(tvShowsJson.getInt(TMDB_NUMBER_OF_EPISODES));
            item.setNumberOfSeasons(tvShowsJson.getInt(TMDB_NUMBER_OF_SEASONS));
            item.setType(tvShowsJson.getString(TMDB_TYPE));
            item.setStatus(tvShowsJson.getString(TMDB_STATUS));
            item.setProductionCompaniesNames(productionCompaniesNames);
            item.setProductionCountriesNames(productionCountriesNames);
            item.setNetworks(networksList);
            item.setOriginCountryList(originCountryList);
            item.setSeasonList(seasonList);
            try {
                item.setDate(formatter.parse(tvShowsJson.getString(TMDB_FIRST_AIR_DATE)));
            } catch (ParseException e) {
                Log.d(LOG_TAG, "Empty date, most likely");
            }
            result.add(item);
        }

        return result;
    }

    private Uri uriByFilterOrId(String filter, String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/tv/" + filter + "?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();
    }

    private Uri uriByName(String name, String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/search/tv?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(NAME_PARAM, name)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();
    }

    private Uri uriOnAir(String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/tv/on_the_air?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();
    }

    private Uri uriForCustomFilter(String language, String page,
                                   String genres, String releaseDateGTE, String releaseDateLTE,
                                   String sortBy) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/tv?";
        Uri.Builder uri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page);
        if (releaseDateLTE != null) {
            uri.appendQueryParameter(RELEASE_DATE_LTE, releaseDateLTE);
        }
        if (releaseDateGTE != null) {
            uri.appendQueryParameter(RELEASE_DATE_GTE, releaseDateGTE);
        }
        if (genres != null && genres.length() != 0) {
            uri.appendQueryParameter(WITH_GENRES, genres);
        }
        if (sortBy != null && sortBy.length() != 0) {
            uri.appendQueryParameter(SORT_BY, sortBy);
        }
        return uri.build();
    }

    private Uri uriByGenres(String genreIDs, String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/tv?";
        //TODO add sort
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(WITH_GENRES, genreIDs)
                .build();
    }

    private Uri uriForSimilar(String tvId, String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/tv/" + tvId + "/similar?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();
    }

    private Uri uriByKeywords(String keywords, String language, String page) {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/tv?";
        return Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(WITH_KEYWORDS, keywords)
                .build();
    }


    private void invokeEvent(List<? extends Media> result) {
        for (TaskCompletedListener listener : listeners)
            listener.mediaLoaded(result, newResult);
    }

}
