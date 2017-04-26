package ru.surf.course.movierecommendations.ui.screen.editPresets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.agna.ferro.mvp.component.ScreenComponent;
import java.util.List;
import javax.inject.Inject;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.screen.editPresets.adapters.EditPresetsAdapter;

/**
 * Created by sergey on 23.04.17.
 */

public class EditPresetsView extends BaseActivityView {

  @Inject
  EditPresetsPresenter presenter;

  private RecyclerView customFilterList;
  private EditPresetsAdapter adapter;
  private FloatingActionButton showOptions;
  private FloatingActionButton addMovieFilter;
  private FloatingActionButton addTVShowFilter;
  private Animation show_fab_1;
  private Animation hide_fab_1;
  private Animation show_fab_2;
  private Animation hide_fab_2;



  public static void start(Context context) {
    Intent intent = new Intent(context, EditPresetsView.class);
    context.startActivity(intent);
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
    return R.layout.activity_edit_presets;
  }

  @Override
  protected ScreenComponent createScreenComponent() {
    return DaggerEditPresetsComponent.builder()
        .activityModule(getActivityModule())
        .appComponent(getAppComponent())
        .build();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean viewRecreated) {
    super.onCreate(savedInstanceState, viewRecreated);
    setupToolbar();
    init();
    setupView();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return presenter.onOptionsItemSelected(item.getItemId());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == EditPresetsPresenter.CREATE_GENRES) {
      if (resultCode == RESULT_OK) {
        presenter.setCustomFilterList();
      }
    }
  }

  private void init() {
    customFilterList = (RecyclerView) findViewById(R.id.edit_presets_rv);
    showOptions = (FloatingActionButton) findViewById(R.id.edit_presets_fab);
    addMovieFilter = (FloatingActionButton) findViewById(R.id.fab_add_movie_filter);
    addTVShowFilter = (FloatingActionButton) findViewById(R.id.fab_add_tvshow_filter);
    show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
    hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
    show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
    hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
  }

  private void setupView() {
    addMovieFilter.setOnClickListener(v -> presenter.onAddFilterBtnClick(MediaType.movie));
    addTVShowFilter.setOnClickListener(v -> presenter.onAddFilterBtnClick(MediaType.tv));
    showOptions.setOnClickListener(v -> presenter.onExpandFabBtnClick());
  }

  public void setupRV(List<CustomFilter> filters) {
    adapter = new EditPresetsAdapter(filters, this, presenter.getDbHelper());
    customFilterList.setAdapter(adapter);
    customFilterList.setLayoutManager(new LinearLayoutManager(this));
  }

  public void setCustomFilterList(List<CustomFilter> filterList) {
    adapter.setCustomFilters(filterList);
  }

  private void setupToolbar() {
    getSupportActionBar().setTitle(R.string.edit_presets);
  }

  public void expandFAB() {

    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) addMovieFilter
        .getLayoutParams();
    layoutParams.rightMargin += (int) (addMovieFilter.getWidth() * 1.7);
    layoutParams.bottomMargin += (int) (addMovieFilter.getHeight() * 0.25);
    addMovieFilter.setLayoutParams(layoutParams);
    addMovieFilter.startAnimation(show_fab_1);
    addMovieFilter.setClickable(true);

    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) addTVShowFilter
        .getLayoutParams();
    layoutParams2.rightMargin += (int) (addTVShowFilter.getWidth() * 1.5);
    layoutParams2.bottomMargin += (int) (addTVShowFilter.getHeight() * 1.5);
    addTVShowFilter.setLayoutParams(layoutParams2);
    addTVShowFilter.startAnimation(show_fab_2);
    addTVShowFilter.setClickable(true);

  }

  public void hideFAB() {

    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) addMovieFilter
        .getLayoutParams();
    layoutParams.rightMargin -= (int) (addMovieFilter.getWidth() * 1.7);
    layoutParams.bottomMargin -= (int) (addMovieFilter.getHeight() * 0.25);
    addMovieFilter.setLayoutParams(layoutParams);
    addMovieFilter.startAnimation(hide_fab_1);
    addMovieFilter.setClickable(false);

    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) addTVShowFilter
        .getLayoutParams();
    layoutParams2.rightMargin -= (int) (addTVShowFilter.getWidth() * 1.5);
    layoutParams2.bottomMargin -= (int) (addTVShowFilter.getHeight() * 1.5);
    addTVShowFilter.setLayoutParams(layoutParams2);
    addTVShowFilter.startAnimation(hide_fab_2);
    addTVShowFilter.setClickable(false);

  }


}
