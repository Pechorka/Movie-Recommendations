package ru.surf.course.movierecommendations.ui.screen.mediaList;

import static android.app.Activity.RESULT_OK;
import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;
import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.DESC;
import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.POPULARITY;
import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_MEDIA;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.interactor.GetMovieTaskRetrofit;
import ru.surf.course.movierecommendations.interactor.GetTVShowTaskRetrofit;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Tasks;
import ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityView;
import ru.surf.course.movierecommendations.ui.screen.mediaList.adapters.GridMediaAdapter;
import ru.surf.course.movierecommendations.ui.screen.mediaList.listeners.EndlessRecyclerViewScrollListener;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sergey on 12.02.2017.
 */

public class MediaListFragment<T extends Media> extends Fragment {

  public static final int GET_GENRES_REQUEST = 1;
  public final static String KEY_MAX_YEAR = "maxYear";
  public final static String KEY_MIN_YEAR = "minYear";
  public final static String KEY_GENRES = "genreIds";
  public final static String KEY_SORT_TYPE = "sort_type";
  public final static String KEY_SORT_DIRECTION = "sort_direction";
  private final static String KEY_QUERY = "query";
  private final static String KEY_REGION = "region";
  private final static String KEY_TASK = "task";
  private final static String KEY_MEDIA_ID = "id";
  private List<Media> mediaList;
  private Tasks task;
  private int page;
  private String query;
  private String region;
  private String ids;
  private int id;
  private Media.MediaType mediaType;
  private String maxYear;
  private String minYear;
  private String genreIds;
  private String sort_type;
  private String sort_direction;
  private String apiKey;

  private boolean newResult;

  private GetMovieTaskRetrofit getMovieTaskRetrofit;
  private GetTVShowTaskRetrofit getTVShowTaskRetrofit;


  private RecyclerView recyclerView;
  private GridMediaAdapter gridMediaAdapter;
  private StaggeredGridLayoutManager staggeredGridLayoutManager;
  private FloatingActionButton showCustomFilterOpt;


  public static MediaListFragment newInstance(String query, String region,
      Tasks task, Media.MediaType mediaType) {
    MediaListFragment mediaListFragment = new MediaListFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_QUERY, query);
    bundle.putSerializable(KEY_TASK, task);
    bundle.putSerializable(KEY_MEDIA, mediaType);
    bundle.putString(KEY_REGION, region);
    mediaListFragment.setArguments(bundle);
    return mediaListFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    query = getArguments().getString(KEY_QUERY);
    region = getArguments().getString(KEY_REGION);
    task = (Tasks) getArguments().getSerializable(KEY_TASK);
    id = getArguments().getInt(KEY_MEDIA_ID);
    ids = query;
    mediaType = (Media.MediaType) getArguments().getSerializable(KEY_MEDIA);
    page = 1;
    apiKey = BuildConfig.TMDB_API_KEY;
    maxYear = String.valueOf(new GregorianCalendar().get(Calendar.YEAR));
    minYear = "1930";
    genreIds = "";
    sort_type = POPULARITY;
    sort_direction = DESC;
    mediaList = new ArrayList<>();
    newResult = true;
    RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory
        .createWithScheduler(Schedulers.io());
    Gson gson = new GsonBuilder().create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(rxAdapter)
        .build();
    getMovieTaskRetrofit = retrofit.create(GetMovieTaskRetrofit.class);
    getTVShowTaskRetrofit = retrofit.create(GetTVShowTaskRetrofit.class);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_media_list, container, false);
    initViews(root);
    setupViews();
    loadInformation(task);
    setHasOptionsMenu(true);
    return root;
  }

  @Override
  public void onStop() {
    super.onStop();
    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GET_GENRES_REQUEST) {
      if (resultCode == RESULT_OK) {
        maxYear = String
            .valueOf(data.getIntExtra(KEY_MAX_YEAR, new GregorianCalendar().get(Calendar.YEAR)));
        minYear = String.valueOf(data.getIntExtra(KEY_MIN_YEAR, 1930));
        genreIds = data.getStringExtra(KEY_GENRES);
        sort_type = data.getStringExtra(KEY_SORT_TYPE);
        sort_direction = data.getStringExtra(KEY_SORT_DIRECTION);
        page = 1;
        newResult = true;
        loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), String.valueOf(page), region,
            newResult);
      }
    }
  }

  private void initViews(View root) {
    recyclerView = (RecyclerView) root.findViewById(R.id.media_list_rv);
    if (getActivity().getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT) {
      staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
    } else {
      staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
    }
    showCustomFilterOpt = (FloatingActionButton) root.findViewById(R.id.media_list_fab);
  }

  private void setupViews() {
    gridMediaAdapter = new GridMediaAdapter(getActivity(), new ArrayList<Media>(1), mediaType);
    recyclerView.setLayoutManager(staggeredGridLayoutManager);
    recyclerView.setAdapter(gridMediaAdapter);
    recyclerView
        .addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
          @Override
          public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            MediaListFragment.this.page++;
            loadInformation(task);
          }
        });
    showCustomFilterOpt.setOnClickListener(v -> startCustomFilterActivity());
  }

  private void startCustomFilterActivity() {
    Intent intent = new Intent(getActivity(), CustomFilterActivityView.class);
    intent.putExtra(KEY_SORT_TYPE, sort_type);
    intent.putExtra(KEY_SORT_DIRECTION, sort_direction);
    intent.putExtra(KEY_MEDIA, mediaType);
    intent.putExtra(KEY_MAX_YEAR, Integer.parseInt(maxYear));
    intent.putExtra(KEY_MIN_YEAR, Integer.parseInt(minYear));
    startActivityForResult(intent, GET_GENRES_REQUEST);
  }

  private void loadInformation(Tasks task) {
    switch (task) {
      case SEARCH_BY_FILTER:
        loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), String.valueOf(page), region,
            newResult);
        break;
      case SEARCH_BY_CUSTOM_FILTER:
        loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), String.valueOf(page), region,
            newResult);
        break;
      case SEARCH_BY_GENRE:
      case SEARCH_RECOMMENDED_MEDIA:
        loadMediaInfoByGenreIds(ids, Utilities.getSystemLanguage(), String.valueOf(page), region,
            task,
            newResult);
        break;
      case SEARCH_BY_KEYWORD:
        loadMediaInfoByKeywords(ids, Utilities.getSystemLanguage(), String.valueOf(page), region,
            newResult);
        break;
      case SEARCH_SIMILAR:
        loadMediaInfoById(id, Utilities.getSystemLanguage(), String.valueOf(page), task, newResult);
        break;
      case SEARCH_BY_NAME:
        loadMediaByName(query, Utilities.getSystemLanguage(), String.valueOf(page), newResult);
      default:
        loadMediaByName(query, Utilities.getSystemLanguage(), String.valueOf(page), newResult);
    }
  }

  public void loadMediaByName(String query, String language, String page, boolean newResult) {
    //TODO fix
    switch (mediaType) {
      case movie:
        Observable<MovieInfo.RetrofitResult> movieCall = getMovieTaskRetrofit
            .getMediaByName(mediaType.toString(), query, apiKey, language, page);
        movieCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
      case tv:
        Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTaskRetrofit
            .getMediaByName(mediaType.toString(), query, apiKey, language, page);
        tvshowCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
    }
    task = Tasks.SEARCH_BY_NAME;
    this.newResult = newResult;
  }

  public void loadMediaInfoByFilter(String query, String language, String page, String region,
      boolean newResult) {
    switch (mediaType) {
      case movie:
        Observable<MovieInfo.RetrofitResult> movieCall = getMovieTaskRetrofit
            .getMediaByFilter(mediaType.toString(), query, apiKey, language, page, region);
        movieCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
      case tv:
        Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTaskRetrofit
            .getMediaByFilter(mediaType.toString(), query, apiKey, language, page, region);
        tvshowCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
    }
    this.page = Integer.parseInt(page);
    task = Tasks.SEARCH_BY_FILTER;
    this.newResult = newResult;
  }

  public void loadMediaInfoByGenreIds(String ids, String language, String page, String region,
      Tasks task, boolean newResult) {
    if (task == Tasks.SEARCH_BY_GENRE || task == Tasks.SEARCH_BY_KEYWORD) {
      switch (mediaType) {
        case movie:
          Observable<MovieInfo.RetrofitResult> movieCall = getMovieTaskRetrofit
              .getMediaByGenreIds(mediaType.toString(), apiKey, language, page, region, ids);
          movieCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
              .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
          break;
        case tv:
          Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTaskRetrofit
              .getMediaByGenreIds(mediaType.toString(), apiKey, language, page, region, ids);
          tvshowCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
              .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
          break;
      }
      this.page = Integer.parseInt(page);
      this.task = task;
      this.newResult = newResult;
    }
  }


  private void loadMediaInfoByKeywords(String keywords, String language, String page, String region,
      boolean newResult) {
    switch (mediaType) {
      case movie:
        Observable<MovieInfo.RetrofitResult> movieCall = getMovieTaskRetrofit
            .getMediaByKeywords(mediaType.toString(), apiKey, language, page, region, keywords);
        movieCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
      case tv:
        Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTaskRetrofit
            .getMediaByKeywords(mediaType.toString(), apiKey, language, page, region, keywords);
        tvshowCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
    }
    this.page = Integer.parseInt(page);
    this.newResult = newResult;

  }

  public void loadMediaInfoById(int id, String language, String page, Tasks task,
      boolean newResult) {
    switch (mediaType) {
      case movie:
        Observable<MovieInfo.RetrofitResult> movieCall = getMovieTaskRetrofit
            .getSimilarMedia(mediaType.toString(), id, apiKey, language, page);
        movieCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
      case tv:
        Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTaskRetrofit
            .getSimilarMedia(mediaType.toString(), id, apiKey, language, page);
        tvshowCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
    }
    this.page = Integer.parseInt(page);
    this.task = task;
    this.newResult = newResult;
  }

  public void loadMediaInfoByCustomFilter(String language, String page, String region,
      boolean newResult) {
    String sortBy = sort_type + "." + sort_direction;
    switch (mediaType) {
      case movie:
        Observable<MovieInfo.RetrofitResult> movieCall = getMovieTaskRetrofit
            .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                minYear, maxYear, sortBy);
        movieCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
      case tv:
        Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTaskRetrofit
            .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                minYear, maxYear, sortBy);
        tvshowCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
    }
    this.page = Integer.parseInt(page);
    task = Tasks.SEARCH_BY_CUSTOM_FILTER;
    this.newResult = newResult;
  }


  public void loadMediaInfoByCustomFilter(String language, String page, String region,
      CustomFilter customFilter, boolean newResult) {
    if (mediaType == customFilter.getMediaType()) {
      applyCustomFilter(customFilter);
    }
    String sortBy = sort_type + "." + sort_direction;
    switch (mediaType) {
      case movie:
        Observable<MovieInfo.RetrofitResult> movieCall = getMovieTaskRetrofit
            .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                minYear, maxYear, sortBy);
        movieCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
      case tv:
        Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTaskRetrofit
            .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                minYear, maxYear, sortBy);
        tvshowCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe((retrofitResult) -> dataLoadComplete(retrofitResult.results));
        break;
    }
    this.page = Integer.parseInt(page);
    task = Tasks.SEARCH_BY_CUSTOM_FILTER;
    this.newResult = newResult;
  }

  private void applyCustomFilter(CustomFilter customFilter) {
    sort_type = customFilter.getSortType();
    sort_direction = customFilter.getSortDirection();
    genreIds = customFilter.getGenreIds();
    maxYear = customFilter.getMaxYear();
    minYear = customFilter.getMinYear();
  }

  public void setCallOptionsVisibility(int visibility) {
    showCustomFilterOpt.setVisibility(visibility);
  }

  public void dataLoadComplete(List<? extends Media> result) {
    if (newResult) {
      newResult = false;
      mediaList.clear();
    }
    mediaList.addAll(result);
    fillInformation();
    View progressBarPlaceholder = null;
    if (getView() != null) {
      progressBarPlaceholder = getView().findViewById(R.id.movie_list_progress_bar_placeholder);
    }
    if (progressBarPlaceholder != null) {
      progressBarPlaceholder.setVisibility(View.GONE);
    }
  }

  public void fillInformation() {
    gridMediaAdapter.setMediaList(mediaList);
  }


}
