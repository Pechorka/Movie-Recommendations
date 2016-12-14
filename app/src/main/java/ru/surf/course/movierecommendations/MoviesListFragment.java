package ru.surf.course.movierecommendations;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.surf.course.movierecommendations.Adapters.GridMoviesAdapter;
import ru.surf.course.movierecommendations.Adapters.ListMoviesAdapter;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;


public class MoviesListFragment extends Fragment implements GetMoviesTask.TaskCompletedListener {

    private final static String KEY_GRID = "grid";
    private final static String KEY_QUERY = "query";
    private final static String KEY_LANGUAGE = "language";
    private final static String KEY_TASK = "task";
    private final static String KEY_MOVIE_ID = "movie_id";

    private String query;
    private String language;
    private int movie_id;

    private RecyclerView recyclerView;
    private boolean grid;
    private List<MovieInfo> movieInfoList;
    private Tasks task;

    private GridMoviesAdapter gridMoviesAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private ListMoviesAdapter listMoviesAdapter;
    private LinearLayoutManager linearLayoutManager;

    public static MoviesListFragment newInstance(String language, String query, Tasks task) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putString(KEY_QUERY, query);
        bundle.putSerializable(KEY_TASK, task);
        moviesListFragment.setArguments(bundle);
        return moviesListFragment;
    }

    public static MoviesListFragment newInstance(String language, int movie_id, Tasks task) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putInt(KEY_MOVIE_ID, movie_id);
        bundle.putSerializable(KEY_TASK, task);
        moviesListFragment.setArguments(bundle);
        return moviesListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString(KEY_QUERY);
        language = getArguments().getString(KEY_LANGUAGE);
        task = (Tasks) getArguments().getSerializable(KEY_TASK);
        movie_id = getArguments().getInt(KEY_MOVIE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movies_list, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.fragment_popular_rv);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        grid = sharedPref.getBoolean(KEY_GRID, true);
        gridMoviesAdapter = new GridMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
        listMoviesAdapter = new ListMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
        if (grid) {
            switchToGrid();
        } else {
            switchToLinear();
        }
        loadInformation();
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_GRID, grid);
        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_switch_layout:
                if (grid) {
                    switchToLinear(item);
                } else {
                    switchToGrid(item);
                }
                return true;
            case R.id.action_search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                item.expandActionView();
                searchView.requestFocus();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void taskCompleted(List<MovieInfo> result) {
        if (result != null) {
            movieInfoList = result;
            dataLoadComplete();
        }
    }

    private void loadInformation() {
        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(this);
        switch (task) {
            case SEARCH_BY_FILTER:
                getMoviesTask.getMoviesByFilter(language, query);
                break;
            case SEARCH_BY_NAME:
                getMoviesTask.getMoviesByName(query);
                break;
            case SEARCH_BY_ID:
                getMoviesTask.getMovieById(movie_id, language);
                break;
            case SEARCH_BY_GENRE:
                getMoviesTask.getMoviesByGenre(query);
                break;
        }
    }

    private void switchToLinear() {
        recyclerView.setAdapter(listMoviesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listMoviesAdapter);
        grid = false;
    }

    private void switchToGrid() {
        recyclerView.setAdapter(gridMoviesAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(gridMoviesAdapter);
        grid = true;
    }


    private void switchToLinear(MenuItem item) {
        recyclerView.setAdapter(listMoviesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listMoviesAdapter);
        item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_grid_on_black_48dp, null));
        grid = false;
    }

    private void switchToGrid(MenuItem item) {
        recyclerView.setAdapter(gridMoviesAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(gridMoviesAdapter);
        item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_black_48dp, null));
        grid = true;
    }

    public void dataLoadComplete() {
        fillInformation();
        View progressBarPlaceholder = null;
        if (getView() != null)
            progressBarPlaceholder = getView().findViewById(R.id.popular_movie_progress_bar_placeholder);
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

