package ru.surf.course.movierecommendations.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.GridMediaAdapter;
import ru.surf.course.movierecommendations.custom_views.CustomFilterOptions;
import ru.surf.course.movierecommendations.listeners.EndlessRecyclerViewScrollListener;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.tmdbTasks.GetGenresTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetTVShowsTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by Sergey on 12.02.2017.
 */

public class MediaListFragment<T extends Media> extends Fragment implements GetMediaTask.TaskCompletedListener<T> {

    private final static String KEY_QUERY = "query";
    private final static String KEY_GRID_POS = "grid_pos";
    private final static String KEY_LANGUAGE = "language";
    private final static String KEY_REGION = "region";
    private final static String KEY_TASK = "task";
    private final static String KEY_MOVIE_ID = "id";
    private static final String KEY_MOVIE = "movie?";

    private boolean filterSetupOpen;
    private List<T> mediaList;
    private Map<String, Integer> genres;
    private Tasks task;
    private int page;
    private String query;
    private String language;
    private String region;
    private String ids;
    private int id;
    private boolean movie;

    private ChooseGenresDialogFragment genresDialogFragment;
    private ChooseSortDialogFragment sortDialogFragment;
    private ChooseSortDirectionDialogFragment sortDirectionFragment;
    private RecyclerView recyclerView;
    private GridMediaAdapter gridMediaAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Button callOptions;
    private Button showGenres;
    private Button showSort;
    private Button showSortDirection;
    private CustomFilterOptions customFilterOptions;

    public static MediaListFragment newInstance(String query, String language, String region, Tasks task, boolean movie) {
        MediaListFragment mediaListFragment = new MediaListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putString(KEY_QUERY, query);
        bundle.putSerializable(KEY_TASK, task);
        bundle.putBoolean(KEY_MOVIE, movie);
        bundle.putString(KEY_REGION, region);
        mediaListFragment.setArguments(bundle);
        return mediaListFragment;
    }

    public static MediaListFragment newInstance(int id, String language, Tasks task, boolean movie) {
        MediaListFragment mediaListFragment = new MediaListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putInt(KEY_MOVIE_ID, id);
        bundle.putSerializable(KEY_TASK, task);
        bundle.putBoolean(KEY_MOVIE, movie);
        mediaListFragment.setArguments(bundle);
        return mediaListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString(KEY_QUERY);
        language = getArguments().getString(KEY_LANGUAGE);
        region = getArguments().getString(KEY_REGION);
        task = (Tasks) getArguments().getSerializable(KEY_TASK);
        id = getArguments().getInt(KEY_MOVIE_ID);
        ids = query;
        movie = getArguments().getBoolean(KEY_MOVIE);
        page = 1;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_media_list, container, false);
        initViews(root);
        setupViews();
        loadInformation(task);
        if (genres == null) {
            loadGenres();
        }
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
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

    private void initViews(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.media_list_rv);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        } else {
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        }
        callOptions = (Button) root.findViewById(R.id.movie_list_call_options);
        customFilterOptions = (CustomFilterOptions) root.findViewById(R.id.custom_filter_options);
        showGenres = (Button) root.findViewById(R.id.genres_dialog);
        showSort = (Button) root.findViewById(R.id.sort_dialog);
        showSortDirection = (Button) root.findViewById(R.id.sort_direction_dialog);
        genresDialogFragment = new ChooseGenresDialogFragment();
        sortDialogFragment = new ChooseSortDialogFragment();
        sortDirectionFragment = new ChooseSortDirectionDialogFragment();
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
        callOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterSetupOpen) {
                    customFilterOptions.setVisibility(View.VISIBLE);
                    filterSetupOpen = true;
                    callOptions.setText(R.string.hide_filter_setup);
                } else {
                    customFilterOptions.setVisibility(View.GONE);
                    filterSetupOpen = false;
                    callOptions.setText(R.string.show_filter_setup);
                }
            }
        });
        customFilterOptions.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                page = 1;
                loadInformation(task);
            }
        });

        final FragmentManager fm = getActivity().getFragmentManager();
        showGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genresDialogFragment.show(fm, "fragment_genres");
            }
        });
        ChooseGenresDialogFragment.SavePressedListener genresSaved = new ChooseGenresDialogFragment.SavePressedListener() {
            @Override
            public void saved() {
                page = 1;
                loadInformation(task);
            }
        };
        genresDialogFragment.addListener(genresSaved);
        ChooseSortDialogFragment.SavePressedListener sortTypeSaved = new ChooseSortDialogFragment.SavePressedListener() {
            @Override
            public void saved() {
                page = 1;
                loadInformation(task);
            }
        };
        sortDialogFragment.addListener(sortTypeSaved);
        showSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialogFragment.show(fm, "fragment_sort");
            }
        });
        ChooseSortDirectionDialogFragment.SavePressedListener directionSaved = new ChooseSortDirectionDialogFragment.SavePressedListener() {
            @Override
            public void saved() {
                page = 1;
                loadInformation(task);
            }
        };
        sortDirectionFragment.addListener(directionSaved);
        showSortDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDirectionFragment.show(fm, "fragment_direction_sort");
            }
        });
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(gridMediaAdapter);


    }

    private void loadInformation(Tasks task) {
        switch (task) {
            case SEARCH_BY_FILTER:
                loadMediaInfoByFilter(query, language, String.valueOf(page), region);
                break;
            case SEARCH_BY_CUSTOM_FILTER:
                loadMediaInfoByCustomFilter(language, String.valueOf(page), region);
                break;
            case SEARCH_BY_GENRE:
            case SEARCH_BY_KEYWORD:
                loadMediaInfoByIds(ids, language, String.valueOf(page), region, task);
                break;
            case SEARCH_BY_ID:
            case SEARCH_SIMILAR:
                loadMediInfoById(id, language, String.valueOf(page), task);
                break;
            default:
                loadMediaByName(query, language, String.valueOf(page));
        }
    }

    public void loadMediaByName(String query, String language, String page) {
        GetMediaTask getMediaTask;
        if (movie) {
            getMediaTask = new GetMoviesTask();
        } else {
            getMediaTask = new GetTVShowsTask();
        }
        getMediaTask.addListener(this);
        getMediaTask.getMediaByName(query, language, page);
    }

    public void loadMediaInfoByFilter(String query, String language, String page, String region) {
        GetMediaTask getMediaTask;
        if (movie) {
            getMediaTask = new GetMoviesTask();
        } else {
            getMediaTask = new GetTVShowsTask();
        }
        getMediaTask.addListener(this);
        getMediaTask.getMediaByFilter(query, language, page, region);
        this.page = Integer.parseInt(page);
        task = Tasks.SEARCH_BY_FILTER;
    }

    public void loadMediaInfoByIds(String ids, String language, String page, String region, Tasks task) {
        GetMediaTask getMediaTask;
        if (movie) {
            getMediaTask = new GetMoviesTask();
        } else {
            getMediaTask = new GetTVShowsTask();
        }
        getMediaTask.addListener(this);
        switch (task) {
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
        GetMediaTask getMediaTask;
        if (movie) {
            getMediaTask = new GetMoviesTask();
        } else {
            getMediaTask = new GetTVShowsTask();
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
        GetMediaTask getMediaTask;
        if (movie) {
            getMediaTask = new GetMoviesTask();
        } else {
            getMediaTask = new GetTVShowsTask();
        }
        getMediaTask.addListener(this);
        String minYear = customFilterOptions.getMinYear() + "-01-01";
        String maxYear = customFilterOptions.getMaxYear() + "-12-31";
        Set<Integer> selected = genresDialogFragment.getSelected(getActivity());
        StringBuilder genres_ids = new StringBuilder();
        if (selected.size() != 0) {
            String[] genresNames = getActivity().getResources().getStringArray(R.array.genres);
            for (Integer s :
                    selected) {
                if (genres.containsKey(genresNames[s])) {
                    genres_ids.append(genres.get(genresNames[s])).append(",");
                }
            }
        }
        String sortBy = sortDialogFragment.getChosenSort() + "." + sortDirectionFragment.getChosenSortDirection();
        getMediaTask.getMediaByCustomFilter(language, page, genres_ids.toString(), maxYear, minYear, sortBy, region);
        this.page = Integer.parseInt(page);
        task = Tasks.SEARCH_BY_CUSTOM_FILTER;
    }

    public void setCallOptionsVisibility(int visibility) {
        callOptions.setVisibility(visibility);
    }

    public void dataLoadComplete() {
        fillInformation();
        View progressBarPlaceholder = null;
        if (getView() != null)
            progressBarPlaceholder = getView().findViewById(R.id.movie_list_progress_bar_placeholder);
        if (progressBarPlaceholder != null)
            progressBarPlaceholder.setVisibility(View.GONE);
    }

    public void fillInformation() {
        gridMediaAdapter.setMediaList(mediaList);
        gridMediaAdapter.notifyDataSetChanged();

    }

    private void loadGenres() {
        GetGenresTask.TaskCompletedListener listener = new GetGenresTask.TaskCompletedListener() {
            @Override
            public void taskCompleted(Map<String, Integer> result) {
                if (result != null) {
                    genres = result;
                }
            }
        };
        GetGenresTask getGenresTask = new GetGenresTask();
        getGenresTask.addListener(listener);
        getGenresTask.getGenres(Tasks.GET_MOVIE_GENRES);
    }

}
