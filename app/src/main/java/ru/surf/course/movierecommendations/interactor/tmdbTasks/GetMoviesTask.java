package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import android.net.Uri;
import android.util.Log;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.domain.Genre;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.MovieInfo;
import ru.surf.course.movierecommendations.domain.ProductionCompanies;
import ru.surf.course.movierecommendations.domain.ProductionCountries;

/**
 * Created by andrew on 12/3/16.
 */


public class GetMoviesTask extends GetMediaTask {

  protected final String LOG_TAG = getClass().getSimpleName();

  @Override
  protected List<MovieInfo> doInBackground(String... params) {
    if (params.length == 0) {
      return null;
    }

    String languageName;
    if (params.length > 1) {
      languageName = params[1];
    } else {
      languageName = "en";
    }

    String page;
    if (params.length > 2) {
      page = params[2];
    } else {
      page = "1";
    }

    String region;
    if (params.length > 3) {
      region = params[3];
    } else {
      region = "US";
    }

    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;

    String movieJsonStr = null;

    try {

      Uri builtUri;
      params[0] = prepareForUri(params[0]);
      switch (task) {
        case SEARCH_BY_FILTER:
          builtUri = uriByFilter(params[0], languageName, page, region);
          break;
        case SEARCH_BY_ID:
          builtUri = uriById(params[0], languageName, page);
          break;
        case SEARCH_BY_NAME:
          builtUri = uriByName(params[0], languageName, page);
          break;
        case SEARCH_BY_GENRE:
          builtUri = uriByGenreIds(params[0], languageName, page, region);
          break;
        case SEARCH_SIMILAR:
          builtUri = uriForSimilar(params[0], languageName, page);
          break;
        case SEARCH_BY_KEYWORD:
          builtUri = uriByKeywords(params[0], languageName, page, region);
          break;
        case SEARCH_BY_CUSTOM_FILTER:
          builtUri = uriForCustomFilter(languageName, page, region, params[0], params[4], params[5],
              params[6]);
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
      List<MovieInfo> result = parseJson(movieJsonStr);
      fillInInfoLanguage(result, new Locale(languageName));
      newResult = Integer.parseInt(page) <= 1;
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
  protected void onPostExecute(List<? extends Media> movieInfos) {
    invokeEvent(movieInfos);
  }

  @Override
  public void getMediaById(int movieId, String language) {
    isLoadingList = false;
    task = Tasks.SEARCH_BY_ID;
    execute(Integer.toString(movieId), language);
  }

  @Override
  public void getMediaByFilter(String filter, String language, String page, String region) {
    isLoadingList = true;
    task = Tasks.SEARCH_BY_FILTER;
    execute(filter, language, page, region);
  }

  @Override
  public void getMediaByName(String name, String language, String page) {
    isLoadingList = true;
    task = Tasks.SEARCH_BY_NAME;
    execute(name, language, page);
  }

  @Override
  public void getMediaByGenre(String genreIds, String language, String page, String region) {
    isLoadingList = true;
    task = Tasks.SEARCH_BY_GENRE;
    execute(genreIds, language, page, region);
  }

  @Override
  public void getSimilarMedia(int tvId, String language, String page) {
    isLoadingList = true;
    task = Tasks.SEARCH_SIMILAR;
    execute(Integer.toString(tvId), language, page);
  }

  @Override
  public void getMediaByKeywords(String keywordIds, String language, String page, String region) {
    isLoadingList = true;
    task = Tasks.SEARCH_BY_KEYWORD;
    execute(keywordIds, language, page, region);
  }


  @Override
  public void getMediaByCustomFilter(String language, String page, String genres,
      String releaseDateGTE, String releaseDateLTE,
      String sortBy, String region) {
    isLoadingList = true;
    task = Tasks.SEARCH_BY_CUSTOM_FILTER;
    execute(genres, language, page, region, releaseDateLTE, releaseDateGTE, sortBy);

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

      ArrayList<Genre> genresList;
      for (int i = 0; i < movieArray.length(); i++) {
        //get genres
        genres = movieArray.getJSONObject(i).getJSONArray(TMDB_GENRE_IDS);
        genresList = new ArrayList<>();
        for (int k = 0; k < genres.length(); k++) {
          genresList.add(new Genre(genres.getInt(k)));
        }

        item = new MovieInfo(movieArray.getJSONObject(i).getInt(TMDB_ID));
        item.setTitle(movieArray.getJSONObject(i).getString(TMDB_TITLE));
        item.setOriginalTitle(movieArray.getJSONObject(i).getString(TMDB_ORIGINAL_TITLE));
        item.setGenres(genresList);
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
      ArrayList<Genre> genreList = new ArrayList<>();
      genres = movieJson.getJSONArray(TMDB_GENRES);
      for (int k = 0; k < genres.length(); k++) {
        genreList.add(new Genre(
            genres.getJSONObject(k).getInt(TMDB_ID),
            genres.getJSONObject(k).getString(TMDB_NAME)));
      }

      //get production companies =
      JSONArray productionCompanies;
      ArrayList<ProductionCompanies> productionCompaniesResult;
      productionCompanies = movieJson.getJSONArray(TMDB_PRODUCTION_COMPANIES);
      productionCompaniesResult = new ArrayList<>();
      for (int k = 0; k < productionCompanies.length(); k++) {
        productionCompaniesResult.add(
            new ProductionCompanies(productionCompanies.getJSONObject(k).getString(TMDB_NAME), 1));
      }

      //get production countries
      JSONArray productionCountries;
      ArrayList<ProductionCountries> productionCountriesResult;
      productionCountries = movieJson.getJSONArray(TMDB_PRODUCTION_COUNTRIES);
      productionCountriesResult = new ArrayList<>();
      for (int k = 0; k < productionCountries.length(); k++) {
        productionCountriesResult.add(
            new ProductionCountries(productionCountries.getJSONObject(k).getString(TMDB_NAME), ""));
      }

      item = new MovieInfo(movieJson.getInt(TMDB_ID));
      item.setTitle(movieJson.getString(TMDB_TITLE));
      item.setOriginalTitle(movieJson.getString(TMDB_ORIGINAL_TITLE));
      item.setOriginalLanguage(new Locale(movieJson.getString(TMDB_ORIGINAL_LANGUAGE)));
      item.setPosterPath(movieJson.getString(TMDB_POSTER_PATH));
      item.setOverview(movieJson.getString(TMDB_OVERVIEW));
      item.setBackdropPath(movieJson.getString(TMDB_BACKDROP_PATH));
      item.setGenres(genreList);
      item.setVoteAverage(movieJson.getDouble(TMDB_RATING));
      item.setVoteCount(movieJson.getInt(TMDB_VOTE_COUNT));
      item.setBudget(movieJson.getString(TMDB_BUDGET));
      item.setProductionCompaniesNames(productionCompaniesResult);
      item.setProductionCountriesNames(productionCountriesResult);
      item.setRevenue(movieJson.getString(TMDB_REVENUE));
      item.setStatus(movieJson.getString(TMDB_STATUS));
      try {
        item.setRuntime(movieJson.getInt(TMDB_RUNTIME));
      } catch (JSONException e) {
        Log.d(LOG_TAG, "Empty runtime, most likely");
      }
      try {
        item.setDate(formatter.parse(movieJson.getString(TMDB_DATE)));
      } catch (ParseException e) {
        Log.d(LOG_TAG, "Empty date, most likely");
      }
      result.add(item);
    }

    return result;
  }

  private void invokeEvent(List<? extends Media> result) {
    for (TaskCompletedListener listener : listeners) {
      listener.mediaLoaded(result, newResult);
    }
  }

  private String prepareForUri(String filter) {
    return filter.toLowerCase().replace(' ', '_');
  }

  private Uri uriByName(String name, String language, String page) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/search/movie?";
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .appendQueryParameter(NAME_PARAM, name)
        .appendQueryParameter(LANGUAGE_PARAM, language)
        .appendQueryParameter(PAGE_PARAM, page)
        .build();
  }

  private Uri uriByFilter(String filter, String language, String page, String region) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + filter + "?";
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .appendQueryParameter(LANGUAGE_PARAM, language)
        .appendQueryParameter(PAGE_PARAM, page)
        .appendQueryParameter(REGION, region)
        .build();
  }

  private Uri uriById(String id, String language, String page) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "?";
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .appendQueryParameter(LANGUAGE_PARAM, language)
        .appendQueryParameter(PAGE_PARAM, page)
        .build();
  }


  private Uri uriForCustomFilter(String language, String page, String region,
      String genres, String releaseDateGTE, String releaseDateLTE,
      String sortBy) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
    Uri.Builder uri = Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .appendQueryParameter(LANGUAGE_PARAM, language)
        .appendQueryParameter(PAGE_PARAM, page)
        .appendQueryParameter(REGION, region);
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

  private Uri uriByGenreIds(String genreIDs, String language, String page, String region) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
    //TODO add sort
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .appendQueryParameter(LANGUAGE_PARAM, language)
        .appendQueryParameter(PAGE_PARAM, page)
        .appendQueryParameter(WITH_GENRES, genreIDs)
        .appendQueryParameter(REGION, region)
        .build();
  }

  private Uri uriForSimilar(String movieId, String language, String page) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + movieId + "/similar?";
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .appendQueryParameter(LANGUAGE_PARAM, language)
        .appendQueryParameter(PAGE_PARAM, page)
        .build();
  }

  private Uri uriByKeywords(String keywords, String language, String page, String region) {
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
    return Uri.parse(TMDB_BASE_URL).buildUpon()
        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
        .appendQueryParameter(LANGUAGE_PARAM, language)
        .appendQueryParameter(WITH_KEYWORDS, keywords)
        .appendQueryParameter(PAGE_PARAM, page)
        .appendQueryParameter(REGION, region)
        .build();
  }

}
