package ru.surf.course.movierecommendations.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.surf.course.movierecommendations.EndlessRecyclerViewScrollListener;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.GridMoviesAdapter;
import ru.surf.course.movierecommendations.adapters.ListMoviesAdapter;
import ru.surf.course.movierecommendations.custom_views.CustomFilterOptions;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.TVShowInfo;
import ru.surf.course.movierecommendations.tmdbTasks.GetGenresTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetTVShowsTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;


public class MoviesListFragment extends Fragment implements GetMoviesTask.TaskCompletedListener, GetTVShowsTask.TaskCompletedListener {

    private static final String LOG_TAG = MoviesListFragment.class.getSimpleName();

    private final static String KEY_GRID = "grid";
    private final static String KEY_QUERY = "query";
    private final static String KEY_LINEAR_POS = "lin_pos";
    private final static String KEY_GRID_POS = "grid_pos";
    private final static String KEY_LANGUAGE = "language";
    private final static String KEY_TASK = "task";
    private final static String KEY_MOVIE_ID = "id";
    private final static String KEY_GENRES = "genres_id";
    private final static String KEY_DATE_GTE = "date_gte";
    private final static String KEY_DATE_LTE = "date_lte";


    private static int PAGE;
    private String query;
    private String language;
    private int id;
    private String previousFilter;
    private Date date_gte;
    private Date date_lte;
    private boolean grid;
    private boolean filterSetupOpen;
    private List<MovieInfo> movieInfoList;
    private List<TVShowInfo> tvshowsInfoList;
    private Map<String, Integer> genres;
    private Tasks task;
    private ChooseGenresDialogFragment genresDialogFragment;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;


    private RecyclerView recyclerView;
    private SlidingUpPanelLayout panelLayout;
    private FloatingActionButton floatingActionButton;
    private GridMoviesAdapter gridMoviesAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ListMoviesAdapter listMoviesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Button callOptions;
    private Button showGenres;
    private CustomFilterOptions customFilterOptions;
    private boolean newResult;
    private boolean movies;

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

    public static MoviesListFragment newInstance(int id, String language, Date gte, Date lte, Tasks task) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putInt(KEY_MOVIE_ID, id);
        bundle.putSerializable(KEY_TASK, task);
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
        if (movies) {
            loadMovieInformation();
        } else {
            loadTVShowInformation();
        }
        if (genres == null) {
            loadGenres();
        }
        drawerToggle.syncState();
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
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                item.expandActionView();
                searchView.requestFocus();
                return true;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.nav_movies:
                if (!movies) {
                    PAGE = 1;
                    movies = true;
                    loadMovieInformation();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void moviesLoaded(List<MovieInfo> result) {
        if (result != null) {
            if (movieInfoList != null && (checkPreviousFilter(query) || !newResult) && PAGE > 1) {
                movieInfoList.addAll(result);
            } else {
                movieInfoList = result;
                newResult = false;
            }
            dataLoadComplete();
        }
    }

    @Override
    public void tvshowsLoaded(List<TVShowInfo> result) {
        if (result != null) {
            if (tvshowsInfoList != null && (checkPreviousFilter(query) || !newResult) && PAGE > 1) {
                tvshowsInfoList.addAll(result);
            } else {
                tvshowsInfoList = result;
                newResult = false;
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
        newResult = true;
        movies = true;
        panelLayout = (SlidingUpPanelLayout) root.findViewById(R.id.sliding_layout);
        floatingActionButton = (FloatingActionButton) root.findViewById(R.id.movie_list_floating_button);
        callOptions = (Button) root.findViewById(R.id.movie_list_call_options);
        customFilterOptions = (CustomFilterOptions) root.findViewById(R.id.custom_filter_options);
        showGenres = (Button) root.findViewById(R.id.genres_dialog);
        genresDialogFragment = new ChooseGenresDialogFragment();
        mDrawer = (DrawerLayout) root.findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) root.findViewById(R.id.nvView);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.activity_main_toolbar);
        drawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        gridMoviesAdapter = new GridMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1), new ArrayList<TVShowInfo>(1), drawerToggle, true);
        listMoviesAdapter = new ListMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
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
                newResult = true;
                loadMovieInformation();
            }
        });
        setupFiltersBtns(root);
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
                loadMovieInformation();
            }
        };
        genresDialogFragment.addListener(listener);
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
        mDrawer.addDrawerListener(drawerToggle);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_movies:
                if (!movies) {
                    PAGE = 1;
                    movies = true;
                    gridMoviesAdapter.switchContentType(true);
                    loadMovieInformation();
                }
                break;
            case R.id.nav_tv:
                if (movies) {
                    PAGE = 1;
                    movies = false;
                    gridMoviesAdapter.switchContentType(false);
                    loadTVShowInformation();
                }
                break;
        }
        menuItem.setChecked(true);

        mDrawer.closeDrawers();
    }


    private void setupFiltersBtns(View root) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousFilter = query;
                switch (view.getId()) {
                    case R.id.sliding_popular:
                        query = GetMoviesTask.FILTER_POPULAR;
                        getActivity().setTitle(R.string.popular);
                        break;
                    case R.id.sliding_top:
                        query = GetMoviesTask.FILTER_TOP_RATED;
                        getActivity().setTitle(R.string.top);
                        break;
                    case R.id.sliding_upcoming:
                        query = GetMoviesTask.FILTER_UPCOMING;
                        getActivity().setTitle(R.string.upcoming);
                        break;
                    case R.id.sliding_custom:
                        query = GetMoviesTask.FILTER_CUSTOM_FILTER;
                        getActivity().setTitle(R.string.custom);
                        callOptions.setVisibility(View.VISIBLE);
                        task = Tasks.SEARCH_BY_CUSTOM_FILTER;
                        if (!checkPreviousFilter(query)) {
                            newResult = true;
                        }
                        break;

                }
                if (!query.equalsIgnoreCase(GetMoviesTask.FILTER_CUSTOM_FILTER)) {
                    callOptions.setVisibility(View.GONE);
                    task = Tasks.SEARCH_BY_FILTER;
                }
                if (checkPreviousFilter(query)) {
                    return;
                }
                PAGE = 1;
                loadMovieInformation();
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

    private void loadTVShowInformation() {
        GetTVShowsTask getTVShowsTask = new GetTVShowsTask();
        getTVShowsTask.addListener(this);
        switch (task) {
            case SEARCH_BY_FILTER:
                getTVShowsTask.getTVShowsByFilter(query, language, String.valueOf(PAGE));
        }
    }

    private void loadMovieInformation() {
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
                getMoviesTask.getMoviesByCustomFilter(language, String.valueOf(PAGE), genres_ids.toString(), maxYear, minYear);
                break;
        }
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


    private void switchToLinear() {
        recyclerView.setAdapter(listMoviesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listMoviesAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
                previousFilter = query;
                loadMovieInformation();
            }
        };
        grid = false;
    }

    private void switchToGrid() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(gridMoviesAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
                previousFilter = query;
                loadMovieInformation();
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
        if (movies) {
            gridMoviesAdapter.setMovieInfoList(movieInfoList);
            listMoviesAdapter.setMovieInfoList(movieInfoList);
        } else {
            gridMoviesAdapter.setTvShowInfoList(tvshowsInfoList);
        }
        gridMoviesAdapter.notifyDataSetChanged();
        listMoviesAdapter.notifyDataSetChanged();

    }

}

