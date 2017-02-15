package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.activities.MovieActivity;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.view_holders.GridViewHolder;

/**
 * Created by Sergey on 12.02.2017.
 */

public class GridMediaAdapter extends RecyclerView.Adapter<GridViewHolder> {
    private List<? extends Media> mediaList;
    private Context context;

    public GridMediaAdapter(Context context, List<? extends Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
        return new GridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GridViewHolder holder, int position) {
        Media media = mediaList.get(position);
        ImageLoader.putPoster(context, media.getPosterPath(), holder.image, ImageLoader.sizes.w500);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchContent(holder.getAdapterPosition(), view);
            }
        });

        if (Build.VERSION.SDK_INT >= 21)
            holder.image.setTransitionName(context.getString(R.string.transition_image) + position);
    }


    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    private void switchContent(int position, View view) {
        if (mediaList.get(position) instanceof MovieInfo) {
            if (context == null)
                return;
            if (Build.VERSION.SDK_INT >= 21) {
                View sharedView = view.findViewById(R.id.grid_image);
                MovieActivity.start(context, (MovieInfo) mediaList.get(position), new Pair<View, String>(sharedView, sharedView.getTransitionName()));
            }
            else
                MovieActivity.start(context , (MovieInfo) mediaList.get(position));
        }

    }

    public void setMediaList(List<? extends Media> list) {
        mediaList = list;
    }

}
