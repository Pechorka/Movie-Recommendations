package ru.surf.course.movierecommendations.ui.screen.editPresets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
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
  private Button addNewMovieFilter;
  private Button addNewTVShowFilter;
  private EditPresetsAdapter adapter;

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
    addNewMovieFilter.setOnClickListener(v -> presenter.onAddFilterBtnClick(MediaType.movie));
    addNewTVShowFilter.setOnClickListener(v -> presenter.onAddFilterBtnClick(MediaType.tv));
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
    addNewMovieFilter = (Button) findViewById(R.id.edit_presets_add_movie_preset);
    addNewTVShowFilter = (Button) findViewById(R.id.edit_presets_add_tvshow_preset);
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
    Toolbar toolbar = (Toolbar) findViewById(R.id.edit_presets_toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_close_white);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.edit_presets);
  }


}
