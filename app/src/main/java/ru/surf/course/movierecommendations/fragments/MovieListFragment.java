package ru.surf.course.movierecommendations.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.surf.course.movierecommendations.EndlessRecyclerViewScrollListener;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.GridMoviesAdapter;
import ru.surf.course.movierecommendations.adapters.ListMoviesAdapter;
import ru.surf.course.movierecommendations.custom_views.CustomFilterOptions;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.tmdbTasks.GetGenresTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by Sergey on 09.02.2017.
 */

public class MovieListFragment extends Fragment implements GetMoviesTask.TaskCompletedListener {
    private final static String KEY_GRID = "grid";
    private final static String KEY_QUERY = "query";
    private final static String KEY_LINEAR_POS = "lin_pos";
    private final static String KEY_GRID_POS = "grid_pos";
    private final static String KEY_LANGUAGE = "language";
    private final static String KEY_TASK = "task";
    private final static String KEY_MOVIE_ID = "id";

    private boolean grid;
    private boolean filterSetupOpen;
    private List<MovieInfo> movieInfoList;
    private Map<String, Integer> genres;
    private Tasks t;
    private int PAGE;
    private String query;
    private String language;
    private int id;

    private ChooseGenresDialogFragment genresDialogFragment;
    private RecyclerView recyclerView;
    private GridMoviesAdapter gridMoviesAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ListMoviesAdapter listMoviesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Button callOptions;
    private Button showGenres;
    private CustomFilterOptions customFilterOptions;

    public static MovieListFragment newInstance(String query, String language, Tasks task) {
        MovieListFragment movieListFragment = new MovieListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putString(KEY_QUERY, query);
        bundle.putSerializable(KEY_TASK, task);
        movieListFragment.setArguments(bundle);
        return movieListFragment;
    }

    public static MovieListFragment newInstance(int id, String language, Tasks task) {
        MovieListFragment movieListFragment = new MovieListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putInt(KEY_MOVIE_ID, id);
        bundle.putSerializable(KEY_TASK, task);
        movieListFragment.setArguments(bundle);
        return movieListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString(KEY_QUERY);
        language = getArguments().getString(KEY_LANGUAGE);
        t = (Tasks) getArguments().getSerializable(KEY_TASK);
        id = getArguments().getInt(KEY_MOVIE_ID);
        PAGE = 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movies_list, container, false);
        initViews(root);
        if (grid) {
            switchToGrid();
        } else {
            switchToLinear();
        }
        setupViews();
        loadInformation(t);
        if (genres == null) {
            loadGenres();
        }
        return root;
    }

    @Override
    public void moviesLoaded(List<MovieInfo> result, boolean newResult) {
        if (result != null) {
            if (movieInfoList != null && !newResult) {
                movieInfoList.addAll(result);
            } else {
                movieInfoList = result;
            }
            dataLoadComplete();
        }

    }

    private void initViews(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.movie_list_rv);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        grid = sharedPref.getBoolean(KEY_GRID, true);
        callOptions = (Button) root.findViewById(R.id.movie_list_call_options);
        customFilterOptions = (CustomFilterOptions) root.findViewById(R.id.custom_filter_options);
        showGenres = (Button) root.findViewById(R.id.genres_dialog);
        genresDialogFragment = new ChooseGenresDialogFragment();
        gridMoviesAdapter = new GridMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
        listMoviesAdapter = new ListMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
    }

    private void setupViews() {
        recyclerView.addOnScrollListener(scrollListener);
        callOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterSetupOpen) {
                    customFilterOptions.setVisibility(View.VISIBLE);
                    filterSetupOpen = true;
                } else {
                    customFilterOptions.setVisibility(View.GONE);
                    filterSetupOpen = false;
                }
            }
        });
        customFilterOptions.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                PAGE = 1;
                loadInformation(t);
            }
        });

        showGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                genresDialogFragment.show(fm, "fragment_genres");
            }
        });
        ChooseGenresDialogFragment.SavePressedListener listener = new ChooseGenresDialogFragment.SavePressedListener() {
            @Override
            public void saved() {
                PAGE = 1;
                loadInformation(t);
            }
        };
        genresDialogFragment.addListener(listener);

    }

    private void switchToLinear() {
        recyclerView.setAdapter(listMoviesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
                loadInformation(t);
            }
        };
        grid = false;
    }

    private void switchToGrid() {
        recyclerView.setAdapter(gridMoviesAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
                loadInformation(t);
            }
        };
        grid = true;
    }

    private void loadInformation(Tasks task) {
        switch (task) {
            case SEARCH_BY_FILTER:
                loadMoviesInfoByFilter(query, language, String.valueOf(PAGE));
                break;
            case SEARCH_BY_CUSTOM_FILTER:
                loadMovieInfoByCustomFilter(language, String.valueOf(PAGE));
                break;
            default:
                loadMoviesInfoById(id, language, task);
        }
    }

    public void loadMoviesInfoByFilter(String query, String language, String page) {
        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(this);
        getMoviesTask.getMoviesByFilter(query, language, page);
    }

    public void loadMoviesInfoById(int id, String language, Tasks task) {
        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(this);
        switch (task) {
            case SEARCH_BY_ID:
                getMoviesTask.getMovieById(id, language);
                break;
            case SEARCH_BY_GENRE:
                getMoviesTask.getMoviesByGenre(id, language);
                break;
            case SEARCH_SIMILAR:
                getMoviesTask.getSimilarMovies(id, language);
                break;
            case SEARCH_BY_KEYWORD:
                getMoviesTask.getMoviesByKeyword(id, language);
                break;
        }
    }

    public void loadMovieInfoByCustomFilter(String language, String page) {
        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(this);
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
        getMoviesTask.getMoviesByCustomFilter(language, page, genres_ids.toString(), maxYear, minYear);
    }


    public void setCallOptionsVisability(int visability) {
        callOptions.setVisibility(visability);
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
        gridMoviesAdapter.setMovieInfoList(movieInfoList);
        listMoviesAdapter.setMovieInfoList(movieInfoList);
        gridMoviesAdapter.notifyDataSetChanged();
        listMoviesAdapter.notifyDataSetChanged();

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
