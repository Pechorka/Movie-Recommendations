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

import java.util.List;

import ru.surf.course.movierecommendations.Adapters.GridMoviesAdapter;
import ru.surf.course.movierecommendations.Adapters.ListMoviesAdapter;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;


public class MoviesListFragment extends Fragment implements GetMoviesTask.TaskCompletedListener {

    private final String KEY_GRID = "grid";
    private final static String KEY_FILTER = "filter";
    private final static String KEY_LANGUAGE = "language";

    private String filter;
    private String language;
    private RecyclerView recyclerView;
    private boolean grid;
    List<MovieInfo> movieInfoList;

    private GridMoviesAdapter gridMoviesAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private ListMoviesAdapter listMoviesAdapter;
    private LinearLayoutManager linearLayoutManager;

    public static MoviesListFragment newInstance(String language, String filter) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LANGUAGE, language);
        bundle.putString(KEY_FILTER, filter);
        moviesListFragment.setArguments(bundle);
        return moviesListFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter = getArguments().getString(KEY_FILTER);
        language = getArguments().getString(KEY_LANGUAGE);
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

        loadInformation(language, filter);
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
                    switchToLinear();
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_grid_on_black_48dp, null));
                    grid = false;
                } else {
                    switchToGrid();
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_black_48dp, null));
                    grid = true;
                }
                return true;
            case R.id.action_search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                // Expand the search view and request focus
                item.expandActionView();
                searchView.requestFocus();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void taskCompleted(List<MovieInfo> result) {
        if (result != null) {
            movieInfoList = result;
        }
        dataLoadComplete();
    }

    private void loadInformation(String language, String filter) {
        GetMoviesTask task = new GetMoviesTask();
        task.addListener(this);
        task.getMovies(language, filter);
    }


    public void switchToLinear() {
        recyclerView.setAdapter(listMoviesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void switchToGrid() {
        recyclerView.setAdapter(gridMoviesAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
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
        if (gridMoviesAdapter == null) {
            gridMoviesAdapter = new GridMoviesAdapter(getActivity(), movieInfoList);
            listMoviesAdapter = new ListMoviesAdapter(getActivity(), movieInfoList);
        } else {
            gridMoviesAdapter.setMovieInfoList(movieInfoList);
            listMoviesAdapter.setMovieInfoList(movieInfoList);
            gridMoviesAdapter.notifyDataSetChanged();
            listMoviesAdapter.notifyDataSetChanged();
        }
        if (grid) {
            switchToGrid();
        } else {
            switchToLinear();
        }

    }

}

