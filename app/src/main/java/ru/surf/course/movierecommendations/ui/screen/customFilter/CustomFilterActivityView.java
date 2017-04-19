package ru.surf.course.movierecommendations.ui.screen.customFilter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.agna.ferro.mvp.component.ScreenComponent;

import java.util.List;

import javax.inject.Inject;


import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.screen.customFilter.adapters.GenreListAdapter;
import ru.surf.course.movierecommendations.ui.screen.customFilter.widgets.YearsRangeBar;
import ru.surf.course.movierecommendations.domain.genre.Genre;

import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.ASC;
import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.DESC;
import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.POPULARITY;
import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.VOTE_AVERAGE;

public class CustomFilterActivityView extends BaseActivityView {

    @Inject
    CustomFilterActivityPresenter presenter;




    private YearsRangeBar yearsRangeBar;
    private RadioGroup sortRG;
    private RadioGroup sortDirectionRG;
    private RecyclerView genresRV;
    private GenreListAdapter genreListAdapter;
    private LinearLayoutManager linearLayoutManager;


    public static void start(Context context) {
        Intent intent = new Intent(context, CustomFilterActivityView.class);
        context.startActivity(intent);
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerCustomFilterActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_custom_filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean isRecreated) {
        super.onCreate(savedInstanceState, isRecreated);

        setupToolbar();
        init();
    }

    public int getYearsRangeBarMinYear() {
        return yearsRangeBar.getCurMinYear();
    }

    public int getYearsRangeBarMaxYear() {
        return yearsRangeBar.getCurMaxYear();
    }

    public String getCheckedGenres() {
        return genreListAdapter.getChecked();
    }

    public String getSortType() {
        switch (sortRG.getCheckedRadioButtonId()) {
            case R.id.sort_by_popularity:
                return POPULARITY;
            case R.id.sort_by_average_votes:
                return VOTE_AVERAGE;
            default:
                return POPULARITY;
        }
    }

    public String getSortDirection() {
        switch (sortDirectionRG.getCheckedRadioButtonId()) {
            case R.id.ascending_order:
                return ASC;
            case R.id.descending_order:
                return DESC;
            default:
                return DESC;
        }
    }

    public void returnWithResults(Intent intent) {
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    public void showCustomFilterDialog(String title, Media.MediaType mediaType) {
        SaveCustomFilterDialog customFilterDialog = SaveCustomFilterDialog
                .newInstance(getCheckedGenres(), getSortType(), getSortDirection(),
                        String.valueOf(getYearsRangeBarMinYear()),
                        String.valueOf(getYearsRangeBarMaxYear()), mediaType);
        customFilterDialog.show(getFragmentManager(), title);
    }

    public void showMessage(int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show();
    }

    private void initGenresList(List<? extends Genre> genres) {
        genreListAdapter = new GenreListAdapter(genres, this);
        genresRV.setAdapter(genreListAdapter);
        genresRV.setLayoutManager(linearLayoutManager);
    }

    public void setGenresListContent(List<? extends Genre> genresListContent) {
        if (genreListAdapter == null)
            initGenresList(genresListContent);
        else {
            genreListAdapter.setGenreList(genresListContent);
            genreListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_filter_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item.getItemId());
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_filter_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.custom);
    }

    private void init() {

        yearsRangeBar = (YearsRangeBar) findViewById(R.id.custom_filter_years_range_bar);
        sortRG = (RadioGroup) findViewById(R.id.custom_filter_sort_options);
        sortDirectionRG = (RadioGroup) findViewById(R.id.custom_filter_sort_direction_options);
        genresRV = (RecyclerView) findViewById(R.id.custom_filter_genres_rv);

        linearLayoutManager = new LinearLayoutManager(this);

    }

    public void setupSortRG(String sortType) {
        switch (sortType) {
            case POPULARITY:
                sortRG.check(R.id.sort_by_popularity);
                break;
            case VOTE_AVERAGE:
                sortRG.check(R.id.sort_by_average_votes);
                break;
        }
    }

    public void setupSortDirectionRG(String sortDirection) {
        switch (sortDirection) {
            case ASC:
                sortDirectionRG.check(R.id.ascending_order);
                break;
            case DESC:
                sortDirectionRG.check(R.id.descending_order);
                break;
        }
    }

    public void setYearsRangeBarMaxValue(int maxValue) {
        yearsRangeBar.setMaxYear(maxValue);
    }

    public void  setYearsRangeBarMinValue(int minValue) {
        yearsRangeBar.setMinYear(minValue);
    }




}
