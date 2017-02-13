package ru.surf.course.movierecommendations.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.GridMediaAdapter;
import ru.surf.course.movierecommendations.adapters.ListMediaAdapter;
import ru.surf.course.movierecommendations.fragments.MediaListFragment;
import ru.surf.course.movierecommendations.listeners.EndlessRecyclerViewScrollListener;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.tmdbTasks.GetMediaTask;

public class SearchResultActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private GridMediaAdapter gridMediaAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ListMediaAdapter listMediaAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int PAGE;

    private boolean grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        String query = null;
        if (getIntent().hasExtra(MainActivity.KEY_SEARCH_QUERY)) {
            query = getIntent().getStringExtra(MainActivity.KEY_SEARCH_QUERY);
        }
        initViews();
        if (grid) {
            switchToGrid();
        } else {
            switchToLinear();
        }
        setupViews();
    }

    private void loadSearchResults(String request, String language, String page) {
        GetMediaTask getMediaTask;
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.movie_list_rv);
        linearLayoutManager = new LinearLayoutManager(this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        grid = sharedPref.getBoolean(MediaListFragment.KEY_GRID, true);
        gridMediaAdapter = new GridMediaAdapter(this, new ArrayList<Media>(1));
        listMediaAdapter = new ListMediaAdapter(this, new ArrayList<Media>(1));
        PAGE = 1;
    }

    private void setupViews() {
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void switchToLinear() {
        recyclerView.setAdapter(listMediaAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listMediaAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
//                loadSearchResults();
            }
        };
        grid = false;
    }

    private void switchToGrid() {
        recyclerView.setAdapter(gridMediaAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(gridMediaAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE++;
//                loadSearchResults();
            }
        };
        grid = true;
    }
}
