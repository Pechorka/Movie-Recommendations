package ru.surf.course.movierecommendations.ui.screen.editPresets.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.ui.screen.editPresets.adapters.EditPresetsAdapter.EditPresetViewHolder;

/**
 * Created by sergey on 23.04.17.
 */

public class EditPresetsAdapter extends RecyclerView.Adapter<EditPresetViewHolder> {

  private List<CustomFilter> customFilters;
  private EditPresetViewHolder editedNow;
  private Context context;
  private DBHelper helper;

  public EditPresetsAdapter(
      List<CustomFilter> customFilters, Context context, DBHelper helper) {
    this.customFilters = customFilters;
    this.context = context;
    this.helper = helper;
  }

  @Override
  public EditPresetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.edit_presets_item, parent, false);
    return new EditPresetViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(EditPresetViewHolder holder, int position) {
    CustomFilter filter = customFilters.get(position);
    holder.filterName.setText(filter.getFilterName());
    setOnClickListeners(holder);
  }

  @Override
  public int getItemCount() {
    return customFilters.size();
  }

  private void setOnClickListeners(EditPresetViewHolder holder) {
    holder.edit.setOnClickListener(
        v -> updateFilter(holder.getAdapterPosition(), holder.filterName.getText().toString()));
    holder.delete.setOnClickListener(v -> delete(holder.getAdapterPosition()));
  }

  private void updateFilter(int position, String name) {
    CustomFilter filter = customFilters.get(position);
    filter.setFilterName(name);
    helper.updateCustomFilter(filter);
    DBHelper.newPreset = true;
  }

  private void delete(int position) {
    CustomFilter filter = customFilters.get(position);
    Dialog confirmationDialog = buildConfirmatoryDialog(filter);
    confirmationDialog.show();
    DBHelper.newPreset = true;
  }

  private Dialog buildConfirmatoryDialog(CustomFilter customFilter) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.confirmatory_dialog_title)
        .setPositiveButton(android.R.string.yes,
            (dialog, which) -> {
              helper.deleteCustomFilter(customFilter);
              customFilters.remove(customFilter);
              notifyDataSetChanged();
            })
        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
    return builder.create();
  }

  public void setCustomFilters(List<CustomFilter> filters) {
    customFilters = filters;
    notifyDataSetChanged();
  }

  public static class EditPresetViewHolder extends RecyclerView.ViewHolder {

    public ImageView delete;
    public ImageView edit;
    public EditText filterName;

    public EditPresetViewHolder(View itemView) {
      super(itemView);
      delete = (ImageView) itemView.findViewById(R.id.edit_presets_item_delete_iv);
      edit = (ImageView) itemView.findViewById(R.id.edit_presets_item_edit_iv);
      filterName = (EditText) itemView.findViewById(R.id.edit_presets_item_tv);
    }
  }

}
