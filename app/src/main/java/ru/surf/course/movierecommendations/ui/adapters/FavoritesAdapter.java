package ru.surf.course.movierecommendations.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.ui.activities.MovieActivity;
import ru.surf.course.movierecommendations.ui.activities.TvShowActivity;
import ru.surf.course.movierecommendations.interactor.Favorite;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;

/**
 * Created by Sergey on 26.03.2017.
 */

public class FavoritesAdapter extends
    RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> implements
    GetMediaTask.TaskCompletedListener<Media> {

  private List<Favorite> favoriteList;
  private Context context;

  private Media loadedInfo;

  public FavoritesAdapter(Context context, List<Favorite> favoriteList) {
    this.favoriteList = favoriteList;
    this.context = context;
  }

  @Override
  public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.grid_element, parent, false);
    return new FavoriteViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final FavoriteViewHolder holder, int position) {
    final Favorite favorite = favoriteList.get(position);
    ImageLoader.putPoster(context, favorite.getPosterPath(), holder.image, ImageLoader.sizes.w500);
    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fragmentToSwitch(favorite);
      }
    });
  }

  @Override
  public int getItemCount() {
    return favoriteList.size();
  }

  @Override
  public void mediaLoaded(List<Media> result, boolean newResult) {
    if (result != null) {
      loadedInfo = result.get(0);
    }
  }


  private void fragmentToSwitch(Favorite favorite) {
    switch (favorite.getMediaType()) {
      case movie:
        MovieActivity.start(context, favorite.getMediaId());
        break;
      case tv:
        TvShowActivity.start(context, favorite.getMediaId());
        break;
      default:
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
    }
  }


  public static class FavoriteViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public CardView cardView;

    public FavoriteViewHolder(View itemView) {
      super(itemView);

      image = (ImageView) itemView.findViewById(R.id.grid_image);
      cardView = (CardView) itemView.findViewById(R.id.grid_cv);
    }
  }
}
