package ru.surf.course.movierecommendations.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.surf.course.movierecommendations.EndlessRecyclerViewScrollListener;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.GridMoviesAdapter;
import ru.surf.course.movierecommendations.adapters.ListMoviesAdapter;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;


public class MoviesListFragment extends Fragment implements GetMoviesTask.TaskCompletedListener {

    private static final String LOG_TAG = MoviesListFragment.class.getSimpleName();

    private final static String KEY_GRID = "grid";
    private final static String KEY_QUERY = "query";
    private final static String KEY_LINEAR_POS = "lin_pos";
    private final static String KEY_GRID_POS = "grid_pos";
    private final static String KEY_LANGUAGE = "language";
    private final static String KEY_TASK = "task";
    private final static String KEY_MOVIE_ID = "id";
    private final static String KEY_GENRES = "genres";
    private final static String KEY_DATE_GTE = "date_gte";
    private final static String KEY_DATE_LTE = "date_lte";


    private static int PAGE;
    private String query;
    private String language;
    private int id;
    private String previousFilter;
    private String genres;
    private Date date_gte;
    private Date date_lte;
    private boolean grid;
    private List<MovieInfo> movieInfoList;
    private Tasks task;

    private RecyclerView recyclerView;
    private SlidingUpPanelLayout panelLayout;
    private FloatingActionButton floatingActionButton;
    private GridMoviesAdapter gridMoviesAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ListMoviesAdapter listMoviesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    public static MoviesListFragment newInstance(String query, String language, Tasks task) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putString(KEY_QUERY, query);
        bundle.putSerializable(KEY_TASK, task);
        moviesListFragment.setArguments(bundle);
        return moviesListFragment;
    }

    public static MoviesListFragment newInstance(int id, String language, Tasks task) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putInt(KEY_MOVIE_ID, id);
        bundle.putSerializable(KEY_TASK, task);
        moviesListFragment.setArguments(bundle);
        return moviesListFragment;
    }

    public static MoviesListFragment newInstance(int id, String language, String genres, Date gte, Date lte, Tasks task) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putInt(KEY_MOVIE_ID, id);
        bundle.putSerializable(KEY_TASK, task);
        bundle.putString(KEY_GENRES, genres);
        bundle.putSerializable(KEY_DATE_LTE, lte);
        bundle.putSerializable(KEY_DATE_GTE, gte);
        moviesListFragment.setArguments(bundle);
        return moviesListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString(KEY_QUERY);
        language = getArguments().getString(KEY_LANGUAGE);
        task = (Tasks) getArguments().getSerializable(KEY_TASK);
        id = getArguments().getInt(KEY_MOVIE_ID);
        PAGE = 1;
        previousFilter = query;
        genres = getArguments().getString(KEY_GENRES);
        date_lte = (Date) getArguments().getSerializable(KEY_DATE_LTE);
        date_gte = (Date) getArguments().getSerializable(KEY_DATE_GTE);
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
        setupViews(root);
        loadInformation();
        return root;
    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_GRID, grid);
        int scrollPositionLinear = -1;
        int scrollPositionGrid = -1;
        if (staggeredGridLayoutManager != null && linearLayoutManager != null) {
            scrollPositionLinear = linearLayoutManager.findFirstVisibleItemPosition();
            scrollPositionGrid = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];
        }
        editor.putInt(KEY_GRID_POS, scrollPositionGrid);
        editor.putInt(KEY_LINEAR_POS, scrollPositionLinear);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (staggeredGridLayoutManager != null && linearLayoutManager != null) {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            int lin_pos = sharedPref.getInt(KEY_LINEAR_POS, -1);
            int grid_pos = sharedPref.getInt(KEY_GRID_POS, -1);
            staggeredGridLayoutManager.scrollToPosition(grid_pos);
            linearLayoutManager.scrollToPosition(lin_pos);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                item.expandActionView();
                searchView.requestFocus();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void taskCompleted(List<MovieInfo> result) {
        if (result != null) {
            if (movieInfoList != null && previousFilter.equalsIgnoreCase(query)) {
                if (PAGE > 1) {
                    movieInfoList.addAll(result);
                }
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
        gridMoviesAdapter = new GridMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
        listMoviesAdapter = new ListMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
        panelLayout = (SlidingUpPanelLayout) root.findViewById(R.id.sliding_layout);
        floatingActionButton = (FloatingActionButton) root.findViewById(R.id.movie_list_floating_button);
    }

    private void setupViews(View root) {
        root.findViewById(R.id.movie_list_listCover).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (panelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    return true;
                }
                return false;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        panelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // do nothing
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            }
        });
        recyclerView.addOnScrollListener(scrollListener);
        setupFiltersBtns(root);
    }

    private void setupFiltersBtns(View root) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousFilter = query;
                switch (view.getId()) {
                    case R.id.sliding_popular:
                        query = GetMoviesTask.FILTER_POPULAR;
                        getActivity().setTitle("Popular");
                        break;
                    case R.id.sliding_top:
                        query = GetMoviesTask.FILTER_TOP_RATED;
                        getActivity().setTitle("Top Rated");
                        break;
                    case R.id.sliding_upcoming:
                        query = GetMoviesTask.FILTER_UPCOMING;
                        getActivity().setTitle("Upcoming");
                        break;
                    case R.id.sliding_custom:
                        query = GetMoviesTask.FILTER_CUSTOM_FILTER;
                        getActivity().setTitle("Custom Filter");
                        break;

                }
                if (checkPreviousFilter(query)) {
                    return;
                }
                PAGE = 1;
                loadInformation();
                panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        };
        Button filterBtn = (Button) root.findViewById(R.id.sliding_popular);
        filterBtn.setOnClickListener(listener);
        filterBtn = (Button) root.findViewById(R.id.sliding_top);
        filterBtn.setOnClickListener(listener);
        filterBtn = (Button) root.findViewById(R.id.sliding_upcoming);
        filterBtn.setOnClickListener(listener);
        filterBtn = (Button) root.findViewById(R.id.sliding_custom);
        filterBtn.setOnClickListener(listener);
    }

    private boolean checkPreviousFilter(String newFilter) {
        return previousFilter.equalsIgnoreCase(newFilter);
    }

    private void loadInformation() {
        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(this);
        switch (task) {
            case SEARCH_BY_FILTER:
                getMoviesTask.getMoviesByFilter(query, language, String.valueOf(PAGE));
                break;
            case SEARCH_BY_NAME:
                getMoviesTask.getMoviesByName(query);
                break;
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
            case SEARCH_BY_CUSTOM_FILTER:
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
                getMoviesTask.getMoviesByCustomFilter(language, String.valueOf(PAGE), genres, dateFormat.format(date_gte), dateFormat.format(date_lte));
                break;
        }
    }

    private void switchToLinear() {
        recyclerView.setAdapter(listMoviesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listMoviesAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
                loadInformation();
            }
        };
        grid = false;
    }

    private void switchToGrid() {
        recyclerView.setAdapter(gridMoviesAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(gridMoviesAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
                loadInformation();
            }
        };
        grid = true;
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
}

