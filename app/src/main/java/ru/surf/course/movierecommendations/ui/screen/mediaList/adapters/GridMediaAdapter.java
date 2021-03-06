package ru.surf.course.movierecommendations.ui.screen.mediaList.adapters;

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
import ru.surf.course.movierecommendations.ui.screen.movie.MovieActivityView;
import ru.surf.course.movierecommendations.ui.screen.tvShow.TvShowActivityView;


public class GridMediaAdapter extends RecyclerView.Adapter<GridMediaAdapter.GridViewHolder> {

    private List<Media> mediaList;
    private Context context;

    public GridMediaAdapter(Context context, List<Media> mediaList) {
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
        holder.cardView.setOnClickListener(view -> fragmentToSwitch(media));
    }


    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    private void fragmentToSwitch(Media media) {
        if (media instanceof MovieInfo) {
            MovieActivityView.start(context, (MovieInfo) media);
        } else if (media instanceof TVShowInfo) {
            TvShowActivityView.start(context, (TVShowInfo) media);
        }

    }

    public void setMediaList(List<Media> list) {
        mediaList = list;
        notifyDataSetChanged();
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
