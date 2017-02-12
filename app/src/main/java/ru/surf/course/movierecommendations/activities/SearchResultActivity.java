package ru.surf.course.movierecommendations.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.surf.course.movierecommendations.R;

public class SearchResultActivity extends AppCompatActivity {

//    RecyclerView recyclerView;
//    private GridMoviesAdapter gridMoviesAdapter;
//    private StaggeredGridLayoutManager staggeredGridLayoutManager;
//    private ListMoviesAdapter listMoviesAdapter;
//    private LinearLayoutManager linearLayoutManager;
//    private EndlessRecyclerViewScrollListener scrollListener;

    private boolean grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        String query = null;
        if (getIntent().hasExtra(MainActivity.KEY_SEARCH_QUERY)) {
            query = getIntent().getStringExtra(MainActivity.KEY_SEARCH_QUERY);
        }
//        initViews();
//        if (grid) {
//            switchToGrid();
//        } else {
//            switchToLinear();
//        }
//        setupViews();
    }

//    private void initViews() {
//        recyclerView = (RecyclerView) findViewById(R.id.movie_list_rv);
//        linearLayoutManager = new LinearLayoutManager(this);
//        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        grid = sharedPref.getBoolean(MovieListFragment.KEY_GRID, true);
//        gridMoviesAdapter = new GridMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
//        listMoviesAdapter = new ListMoviesAdapter(getActivity(), new ArrayList<MovieInfo>(1));
//    }
}
