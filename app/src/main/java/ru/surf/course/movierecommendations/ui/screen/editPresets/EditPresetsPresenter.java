package ru.surf.course.movierecommendations.ui.screen.editPresets;

import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_MEDIA;

import android.content.Intent;
import com.agna.ferro.mvp.component.scope.PerScreen;
import java.util.List;
import javax.inject.Inject;
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityView;

/**
 * Created by sergey on 23.04.17.
 */

@PerScreen
public class EditPresetsPresenter extends BasePresenter<EditPresetsView> {

  public static final int CREATE_GENRES = 1;
  public static final String KEY_REQUEST_CODE = "request_code";

  private DBHelper dbHelper;

  @Inject
  public EditPresetsPresenter(
      ErrorHandler errorHandler,
      DBHelper dbHelper) {
    super(errorHandler);
    this.dbHelper = dbHelper;
  }

  @Override
  public void onLoad(boolean viewRecreated) {
    super.onLoad(viewRecreated);
    setupRV();
  }

  private void setupRV() {
    List<CustomFilter> filters = dbHelper.getAllCustomFilters();
    if (filters != null && filters.size() != 0) {
      getView().setupRV(filters);
    }
  }

  public void setCustomFilterList() {
    List<CustomFilter> filters = dbHelper.getAllCustomFilters();
    getView().setCustomFilterList(filters);
  }

  public boolean onOptionsItemSelected(int id) {
    switch (id) {
      case android.R.id.home:
        getView().onBackPressed();
        return true;
      default:
        return false;
    }
  }

  public DBHelper getDbHelper() {
    return dbHelper;
  }

  public void onAddFilterBtnClick(MediaType mediaType) {
    Intent intent = new Intent(getView(), CustomFilterActivityView.class);
    intent.putExtra(KEY_MEDIA, mediaType);
    intent.putExtra(KEY_REQUEST_CODE, CREATE_GENRES);
    getView().startActivityForResult(intent, CREATE_GENRES);
  }
}
