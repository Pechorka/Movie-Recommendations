package ru.surf.course.movierecommendations.ui.screen.customFilter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.genre.Genre;


public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreViewHolder> {
    private List<? extends Genre> genreList;

    public GenreListAdapter(List<? extends Genre> genreList, Context context) {
        this.genreList = genreList;
        Context context1 = context;
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_list_element, parent, false);
        return new GenreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        Genre genre = genreList.get(position);
        holder.genreName.setText(genre.getName());
        if (genre.isChecked()) {
            holder.checkBox.setChecked(true);
        }
        holder.checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> genre.setChecked(isChecked));

        holder.genreName.setOnClickListener(v -> {
            genre.reverseChecked();
            holder.checkBox.setChecked(genre.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public void setGenreList(List<? extends Genre> genreList) {
        this.genreList = genreList;
        notifyDataSetChanged();
    }

    public String getChecked() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Genre genre : genreList) {
            if (genre.isChecked()) {
                stringBuilder.append(genre.getGenreId()).append(",");
            }
        }
        return stringBuilder.toString();
    }

  public void checkChosenGenres(String genreIds) {
    for (Genre genre : genreList) {
      if (genreIds.contains(String.valueOf(genre.getGenreId()))) {
        genre.setChecked(true);
      }
    }
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
