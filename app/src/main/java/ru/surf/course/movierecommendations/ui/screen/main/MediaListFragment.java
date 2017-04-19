package ru.surf.course.movierecommendations.ui.screen.main;

import static android.app.Activity.RESULT_OK;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowsTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Tasks;
import ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivity;
import ru.surf.course.movierecommendations.ui.screen.main.adapters.GridMediaAdapter;
import ru.surf.course.movierecommendations.ui.screen.main.listeners.EndlessRecyclerViewScrollListener;
import ru.surf.course.movierecommendations.util.Utilities;

/**
 * Created by Sergey on 12.02.2017.
 */

public class MediaListFragment<T extends Media> extends Fragment implements
    GetMediaTask.TaskCompletedListener<T> {

  public static final int GET_GENRES_REQUEST = 1;
  public final static String KEY_MAX_YEAR = "maxYear";
  public final static String KEY_MIN_YEAR = "minYear";
  public final static String KEY_GENRES = "genre_ids";
  public final static String KEY_SORT_TYPE = "sort_type";
  public final static String KEY_SORT_DIRECTION = "sort_direction";
  private final static String KEY_QUERY = "query";
  private final static String KEY_GRID_POS = "grid_pos";
  private final static String KEY_REGION = "region";
  private final static String KEY_TASK = "task";
  private final static String KEY_MEDIA_ID = "id";
  private List<T> mediaList;
  private Tasks task;
  private int page;
  private String query;
  private String region;
  private String ids;
  private int id;
  private MediaType mediaType;
  private String maxYear;
  private String minYear;
  private String genre_ids;
  private String sort_type;
  private String sort_direction;


  private RecyclerView recyclerView;
  private GridMediaAdapter gridMediaAdapter;
  private StaggeredGridLayoutManager staggeredGridLayoutManager;
  private EndlessRecyclerViewScrollListener scrollListener;
  private FloatingActionButton showCustomFilterOpt;

  public static MediaListFragment newInstance(String query, String region,
      Tasks task, MediaType mediaType) {
    MediaListFragment mediaListFragment = new MediaListFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_QUERY, query);
    bundle.putSerializable(KEY_TASK, task);
    bundle.putSerializable(MainActivity.KEY_MEDIA, mediaType);
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
    mediaType = (MediaType) getArguments().getSerializable(MainActivity.KEY_MEDIA);
    page = 1;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_media_list, container, false);
    initFields();
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
        genre_ids = data.getStringExtra(KEY_GENRES);
        sort_type = data.getStringExtra(KEY_SORT_TYPE);
        sort_direction = data.getStringExtra(KEY_SORT_DIRECTION);
        page = 1;
        loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), String.valueOf(page), region);
      }
    }
  }

  @Override
  public void mediaLoaded(List<T> result, boolean newResult) {
    if (result != null) {
      if (mediaList != null && !newResult) {
        mediaList.addAll(result);
      } else {
        mediaList = result;
      }
      dataLoadComplete();
    }
  }

  private void initFields() {
    maxYear = String.valueOf(new GregorianCalendar().get(Calendar.YEAR));
    minYear = "1930";
    genre_ids = "";
    sort_type = CustomFilterActivity.POPULARITY;
    sort_direction = CustomFilterActivity.DESC;
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
    gridMediaAdapter = new GridMediaAdapter(getActivity(), new ArrayList<Media>(1));
    scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        MediaListFragment.this.page++;
        loadInformation(task);
      }
    };
  }

  private void setupViews() {
    recyclerView.addOnScrollListener(scrollListener);
    showCustomFilterOpt.setOnClickListener(v -> {
      Intent intent = new Intent(getActivity(), CustomFilterActivity.class);
      intent.putExtra(KEY_SORT_TYPE, sort_type);
      intent.putExtra(KEY_SORT_DIRECTION, sort_direction);
      intent.putExtra(MainActivity.KEY_MEDIA, mediaType);
      intent.putExtra(KEY_MAX_YEAR, Integer.parseInt(maxYear));
      intent.putExtra(KEY_MIN_YEAR, Integer.parseInt(minYear));
      startActivityForResult(intent, GET_GENRES_REQUEST);
    });
    recyclerView.setLayoutManager(staggeredGridLayoutManager);
    recyclerView.setAdapter(gridMediaAdapter);
  }

  private void loadInformation(Tasks task) {
    switch (task) {
      case SEARCH_BY_FILTER:
        loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), String.valueOf(page), region);
        break;
      case SEARCH_BY_CUSTOM_FILTER:
        loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), String.valueOf(page), region);
        break;
      case SEARCH_BY_GENRE:
      case SEARCH_RECOMMENDED_MEDIA:
      case SEARCH_BY_KEYWORD:
        loadMediaInfoByIds(ids, Utilities.getSystemLanguage(), String.valueOf(page), region, task);
        break;
      case SEARCH_BY_ID:
      case SEARCH_SIMILAR:
        loadMediInfoById(id, Utilities.getSystemLanguage(), String.valueOf(page), task);
        break;
      default:
        loadMediaByName(query, Utilities.getSystemLanguage(), String.valueOf(page));
    }
  }

  public void loadMediaByName(String query, String language, String page) {
    GetMediaTask getMediaTask = null;
    switch (mediaType) {
      case movie:
        getMediaTask = new GetMoviesTask();
        break;
      case tv:
        getMediaTask = new GetTVShowsTask();
        break;
    }
    getMediaTask.addListener(this);
    getMediaTask.getMediaByName(query, language, page);
    task = Tasks.SEARCH_BY_NAME;
  }

  public void loadMediaInfoByFilter(String query, String language, String page, String region) {
    GetMediaTask getMediaTask = null;
    switch (mediaType) {
      case movie:
        getMediaTask = new GetMoviesTask();
        break;
      case tv:
        getMediaTask = new GetTVShowsTask();
        break;
    }
    getMediaTask.addListener(this);
    getMediaTask.getMediaByFilter(query, language, page, region);
    this.page = Integer.parseInt(page);
    task = Tasks.SEARCH_BY_FILTER;
  }

  public void loadMediaInfoByIds(String ids, String language, String page, String region,
      Tasks task) {
    GetMediaTask getMediaTask = null;
    switch (mediaType) {
      case movie:
        getMediaTask = new GetMoviesTask();
        break;
      case tv:
        getMediaTask = new GetTVShowsTask();
        break;
    }
    getMediaTask.addListener(this);
    switch (task) {
      case SEARCH_RECOMMENDED_MEDIA:
      case SEARCH_BY_GENRE:
        getMediaTask.getMediaByGenre(ids, language, page, region);
        break;
      case SEARCH_BY_KEYWORD:
        getMediaTask.getMediaByKeywords(ids, language, page, region);
        break;
    }
    this.page = Integer.parseInt(page);
    this.task = task;
  }

  public void loadMediInfoById(int id, String language, String page, Tasks task) {
    GetMediaTask getMediaTask = null;
    switch (mediaType) {
      case movie:
        getMediaTask = new GetMoviesTask();
        break;
      case tv:
        getMediaTask = new GetTVShowsTask();
        break;
    }
    getMediaTask.addListener(this);
    switch (task) {
      case SEARCH_BY_ID:
        getMediaTask.getMediaById(id, language);
        break;
      case SEARCH_SIMILAR:
        getMediaTask.getSimilarMedia(id, language, page);
        break;
    }
    this.page = Integer.parseInt(page);
    this.task = task;
  }

  public void loadMediaInfoByCustomFilter(String language, String page, String region) {
    GetMediaTask getMediaTask = null;
    switch (mediaType) {
      case movie:
        getMediaTask = new GetMoviesTask();
        break;
      case tv:
        getMediaTask = new GetTVShowsTask();
        break;
    }
    getMediaTask.addListener(this);
    String sortBy = sort_type + "." + sort_direction;
    getMediaTask
        .getMediaByCustomFilter(language, page, genre_ids, maxYear, minYear, sortBy, region);
    this.page = Integer.parseInt(page);
    task = Tasks.SEARCH_BY_CUSTOM_FILTER;
  }


  public void loadMediaInfoByCustomFilter(String language, String page, String region,
      CustomFilter customFilter) {
    GetMediaTask getMediaTask = null;
    switch (mediaType) {
      case movie:
        getMediaTask = new GetMoviesTask();
        break;
      case tv:
        getMediaTask = new GetTVShowsTask();
        break;
    }
    getMediaTask.addListener(this);
    if (mediaType == customFilter.getMediaType()) {
      applyCustomFilter(customFilter);
    }
    String sortBy = sort_type + "." + sort_direction;
    getMediaTask
        .getMediaByCustomFilter(language, page, genre_ids, maxYear, minYear, sortBy, region);
    this.page = Integer.parseInt(page);
    task = Tasks.SEARCH_BY_CUSTOM_FILTER;
  }

  private void applyCustomFilter(CustomFilter customFilter) {
    sort_type = customFilter.getSortType();
    sort_direction = customFilter.getSortDirection();
    genre_ids = customFilter.getGenreIds();
    maxYear = customFilter.getMaxYear();
    minYear = customFilter.getMinYear();
  }

  public void setCallOptionsVisibility(int visibility) {
    showCustomFilterOpt.setVisibility(visibility);
  }

  public void dataLoadComplete() {
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
    gridMediaAdapter.notifyDataSetChanged();

  }


}
