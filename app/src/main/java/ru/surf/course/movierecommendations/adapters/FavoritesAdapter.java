package ru.surf.course.movierecommendations.adapters;

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
import ru.surf.course.movierecommendations.activities.MovieActivity;
import ru.surf.course.movierecommendations.activities.TvShowActivity;
import ru.surf.course.movierecommendations.models.Favorite;
import ru.surf.course.movierecommendations.models.FavoriteType;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.TVShowInfo;
import ru.surf.course.movierecommendations.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetTVShowsTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by Sergey on 26.03.2017.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> implements GetMediaTask.TaskCompletedListener<Media> {

    private List<Favorite> favoriteList;
    private Context context;

    private Media loadedInfo;

    public FavoritesAdapter(Context context, List<Favorite> favoriteList) {
        this.favoriteList = favoriteList;
        this.context = context;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, int position) {
        Favorite media = favoriteList.get(position);
        ImageLoader.putPoster(context, media.getPosterPath(), holder.image, ImageLoader.sizes.w500);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentToSwitch(holder.getAdapterPosition());
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


    private void fragmentToSwitch(int position) {
        Favorite favorite = favoriteList.get(position);
        loadFavoriteInfo(favorite.getId(), favorite.getMediaType());
        if (loadedInfo instanceof MovieInfo) {
            MovieActivity.start(context, (MovieInfo) loadedInfo);
        } else {
            TvShowActivity.start(context, (TVShowInfo) loadedInfo);
        }
    }


    private void loadFavoriteInfo(int id, FavoriteType type) {
        GetMediaTask getTask;
        switch (type) {
            case movie:
                getTask = new GetMoviesTask();
                break;
            case tvShow:
                getTask = new GetTVShowsTask();
                break;
            default:
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                return;
        }
        getTask.getMediaById(id, "en");
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
