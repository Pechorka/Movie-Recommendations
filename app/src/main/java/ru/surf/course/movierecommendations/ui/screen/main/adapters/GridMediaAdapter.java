package ru.surf.course.movierecommendations.ui.screen.main.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.ui.screen.movie.MovieActivity;
import ru.surf.course.movierecommendations.ui.screen.tvShow.TvShowActivity;

/**
 * Created by Sergey on 12.02.2017.
 */

public class GridMediaAdapter extends RecyclerView.Adapter<GridMediaAdapter.GridViewHolder> {

  private List<? extends Media> mediaList;
  private Context context;

  public GridMediaAdapter(Context context, List<? extends Media> mediaList) {
    this.context = context;
    this.mediaList = mediaList;
  }

  @Override
  public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.grid_element, parent, false);
    return new GridViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final GridViewHolder holder, int position) {
    Media media = mediaList.get(position);
    ImageLoader.putPoster(context, media.getPosterPath(), holder.image, ImageLoader.sizes.w500);
    holder.cardView.setOnClickListener(view -> fragmentToSwitch(holder.getAdapterPosition()));
  }


  @Override
  public int getItemCount() {
    return mediaList.size();
  }

  private void fragmentToSwitch(int position) {
    if (mediaList.get(position) instanceof MovieInfo) {
      MovieActivity.start(context, (MovieInfo) mediaList.get(position));
    } else if (mediaList.get(position) instanceof TVShowInfo) {
      TvShowActivity.start(context, (TVShowInfo) mediaList.get(position));
    }
  }

  public void setMediaList(List<? extends Media> list) {
    mediaList = list;
  }

  public static class GridViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public CardView cardView;

    public GridViewHolder(View itemView) {
      super(itemView);

      image = (ImageView) itemView.findViewById(R.id.grid_image);
      cardView = (CardView) itemView.findViewById(R.id.grid_cv);
    }
  }
}
