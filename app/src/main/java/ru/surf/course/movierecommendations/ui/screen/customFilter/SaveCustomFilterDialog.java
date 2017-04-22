package ru.surf.course.movierecommendations.ui.screen.customFilter;

import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_MEDIA;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragment;

/**
 * Created by sergey on 04.04.17.
 */

public class SaveCustomFilterDialog extends DialogFragment {


  public static final String KEY_ADDED_NEW_PRESET = "new_preset";

  private String sortType;
  private String sortDirection;
  private String genreIds;
  private String minYear;
  private String maxYear;
  private MediaType mediaType;

  private EditText filterName;

  public static SaveCustomFilterDialog newInstance(String genreIds, String sortType,
      String sortDirection, String minYear,
      String maxYear, MediaType mediaType) {
    Bundle bundle = new Bundle();
    bundle.putString(MediaListFragment.KEY_GENRES, genreIds);
    bundle.putString(MediaListFragment.KEY_MAX_YEAR, maxYear);
    bundle.putString(MediaListFragment.KEY_MIN_YEAR, minYear);
    bundle.putString(MediaListFragment.KEY_SORT_TYPE, sortType);
    bundle.putString(MediaListFragment.KEY_SORT_DIRECTION, sortDirection);
    bundle.putSerializable(KEY_MEDIA, mediaType);
    SaveCustomFilterDialog dialog = new SaveCustomFilterDialog();
    dialog.setArguments(bundle);
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sortType = getArguments().getString(MediaListFragment.KEY_SORT_TYPE);
    sortDirection = getArguments().getString(MediaListFragment.KEY_SORT_DIRECTION);
    genreIds = getArguments().getString(MediaListFragment.KEY_GENRES);
    minYear = getArguments().getString(MediaListFragment.KEY_MIN_YEAR);
    maxYear = getArguments().getString(MediaListFragment.KEY_MAX_YEAR);
    mediaType = (MediaType) getArguments().getSerializable(KEY_MEDIA);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View layout = inflater.inflate(R.layout.dialog_save_custom_filter, null);
    builder.setView(layout)
        .setTitle(R.string.save_filter_dialog_title)
        .setPositiveButton(R.string.apply, (dialog, id) -> saveCustomFilter())
        .setNegativeButton(R.string.cancel,
            (dialog, id) -> SaveCustomFilterDialog.this.getDialog().cancel())
        .setCancelable(true);
    filterName = (EditText) layout.findViewById(R.id.custom_filter_name);
    return builder.create();
  }

  private void saveCustomFilter() {
    CustomFilter customFilter = new CustomFilter(filterName.getText().toString(), genreIds,
        sortType, sortDirection, minYear, maxYear, mediaType);
    getActivity().getPreferences(Context.MODE_PRIVATE).edit().putBoolean(KEY_ADDED_NEW_PRESET, true)
        .commit();
    DBHelper.getHelper(getActivity()).addMovieCustomFilter(customFilter);
  }
}
