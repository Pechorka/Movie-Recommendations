package ru.surf.course.movierecommendations.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.GenreListAdapter;
import ru.surf.course.movierecommendations.custom_views.YearsRangeBar;
import ru.surf.course.movierecommendations.fragments.MediaListFragment;
import ru.surf.course.movierecommendations.models.Genre;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.tmdbTasks.GetGenresTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

public class CustomFilterActivity extends AppCompatActivity {


    public static final String POPULARITY = "popularity";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String ASC = "asc";
    public static final String DESC = "desc";

    private YearsRangeBar yearsRangeBar;
    private RadioGroup sortRG;
    private RadioGroup sortDirectionRG;
    private RecyclerView genresRV;
    private GenreListAdapter genreListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<Genre> genres;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_filter);
        setupToolbar();
        if(genres==null){
            loadGenres();
        }
        init();
        setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_filter_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
           case R.id.custom_filter_menu_save:
               Intent intent = new Intent();
               intent.putExtra(MediaListFragment.KEY_MIN_YEAR,yearsRangeBar.getCurMinYear());
               intent.putExtra(MediaListFragment.KEY_MAX_YEAR,yearsRangeBar.getCurMaxYear());
               intent.putExtra(MediaListFragment.KEY_GENRES,genreListAdapter.getChecked());
               intent.putExtra(MediaListFragment.KEY_SORT_TYPE,getSortType());
               intent.putExtra(MediaListFragment.KEY_SORT_DIRECTION,getSortDirection());
               setResult(RESULT_OK,intent);
               genreListAdapter.save();
               onBackPressed();
               return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_filter_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.custom);
    }

    private void init() {
        yearsRangeBar = (YearsRangeBar)findViewById(R.id.custom_filter_years_range_bar);
        sortRG = (RadioGroup)findViewById(R.id.custom_filter_sort_options);
        sortDirectionRG = (RadioGroup)findViewById(R.id.custom_filter_sort_direction_options);
        genresRV = (RecyclerView)findViewById(R.id.custom_filter_genres_rv);
        genres = new ArrayList<>();
        genreListAdapter = new GenreListAdapter(genres,this);
        linearLayoutManager = new LinearLayoutManager(this);
    }

    private void setupView(){
        genresRV.setAdapter(genreListAdapter);
        genresRV.setLayoutManager(linearLayoutManager);
        if(getIntent().hasExtra(MediaListFragment.KEY_SORT_TYPE)){
            setupSortRG(getIntent().getStringExtra(MediaListFragment.KEY_SORT_TYPE));
        }
        if(getIntent().hasExtra(MediaListFragment.KEY_SORT_DIRECTION)){
            setupSortDirectionRG(getIntent().getStringExtra(MediaListFragment.KEY_SORT_DIRECTION));
        }
    }

    private void setupSortRG(String sortType){
        switch (sortType){
            case POPULARITY:
                sortRG.check(R.id.sort_by_popularity);
                break;
            case VOTE_AVERAGE:
                sortRG.check(R.id.sort_by_average_votes);
                break;
        }
    }

    private void setupSortDirectionRG(String sortDirection){
        switch (sortDirection){
            case ASC:
                sortDirectionRG.check(R.id.ascending_order);
                break;
            case DESC:
                sortDirectionRG.check(R.id.descending_order);
                break;
        }
    }

    private void loadGenres() {
        GetGenresTask.TaskCompletedListener listener = new GetGenresTask.TaskCompletedListener() {
            @Override
            public void taskCompleted(List<Genre> result) {
                if (result != null) {
                    genres = result;
                    genreListAdapter.setGenreList(genres);
                    genreListAdapter.notifyDataSetChanged();
                }
            }
        };
        GetGenresTask getGenresTask = new GetGenresTask();
        getGenresTask.addListener(listener);
        getGenresTask.getGenres(Tasks.GET_MOVIE_GENRES);
    }

    private String getSortType(){
        switch (sortRG.getCheckedRadioButtonId()){
            case R.id.sort_by_popularity:
                return POPULARITY;
            case R.id.sort_by_average_votes:
                return VOTE_AVERAGE;
            default:
                return POPULARITY;
        }
    }

    private String getSortDirection(){
        switch (sortDirectionRG.getCheckedRadioButtonId()){
            case R.id.ascending_order:
                return ASC;
            case R.id.descending_order:
                return DESC;
            default:
                return DESC;
        }
    }

}
