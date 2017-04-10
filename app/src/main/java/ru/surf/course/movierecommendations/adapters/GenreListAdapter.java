package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.models.Genre;

/**
 * Created by Sergey on 28.02.2017.
 */

public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreViewHolder> {

  public static final String GENRES_PREFS = "genres_prefs";
  private static final String CHECKED_ARRAY = "checked_array";

  private List<Genre> genreList;
  private boolean[] checked;
  private Context context;

  public GenreListAdapter(List<Genre> genreList, Context context) {
    this.genreList = genreList;
    this.context = context;
    checked = loadChecked(context);
  }

  @Override
  public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.genre_list_element, parent, false);
    return new GenreViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final GenreViewHolder holder, int position) {
    Genre genre = genreList.get(position);
    holder.genreName.setText(genre.getName());
    if (checked[position]) {
      holder.checkBox.setChecked(true);
    }
    holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checked[holder.getAdapterPosition()] = isChecked;

      }
    });

    holder.genreName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checked[holder.getAdapterPosition()] = !checked[holder.getAdapterPosition()];
        holder.checkBox.setChecked(checked[holder.getAdapterPosition()]);
      }
    });
  }

  @Override
  public int getItemCount() {
    return genreList.size();
  }

  public void setGenreList(List<Genre> genreList) {
    this.genreList = genreList;
  }

  public String getChecked() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < checked.length; i++) {
      if (checked[i]) {
        stringBuilder.append(genreList.get(i).getGenreId()).append(",");
      }
    }
    return stringBuilder.toString();
  }

  public void save() {
    saveChecked(checked, context);
  }

  private boolean saveChecked(boolean[] array, Context context) {
    SharedPreferences prefs = context.getSharedPreferences(GENRES_PREFS, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putInt(CHECKED_ARRAY + "_size", array.length);

    for (int i = 0; i < array.length; i++) {
      editor.putBoolean(CHECKED_ARRAY + "_" + i, array[i]);
    }

    return editor.commit();
  }


  private boolean[] loadChecked(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(GENRES_PREFS, Context.MODE_PRIVATE);
    int size = prefs.getInt(CHECKED_ARRAY + "_size", 19);
    boolean[] array = new boolean[size];
    for (int i = 0; i < size; i++) {
      array[i] = prefs.getBoolean(CHECKED_ARRAY + "_" + i, false);
    }
    return array;
  }

  public static void clearChecked(Context context){
    SharedPreferences prefs = context.getSharedPreferences(GENRES_PREFS,Context.MODE_PRIVATE);
    Editor editor = prefs.edit();
    int size = prefs.getInt(CHECKED_ARRAY + "_size", 19);
    for (int i = 0; i < size; i++) {
      editor.remove(CHECKED_ARRAY + "_" + i);
    }
    editor.apply();
  }

  public static class GenreViewHolder extends RecyclerView.ViewHolder {

    public CheckBox checkBox;
    public TextView genreName;

    public GenreViewHolder(View itemView) {
      super(itemView);

      checkBox = (CheckBox) itemView.findViewById(R.id.genre_list_checkbox);
      genreName = (TextView) itemView.findViewById(R.id.genre_list_name);
    }
  }
}
