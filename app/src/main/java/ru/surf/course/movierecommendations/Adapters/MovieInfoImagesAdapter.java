package ru.surf.course.movierecommendations.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 12/30/16.
 */

public class MovieInfoImagesAdapter extends RecyclerView.Adapter<MovieInfoImagesAdapter.MyViewHolder> {

    private List<TmdbImage> images;
    private Context context;

    public MovieInfoImagesAdapter(List<TmdbImage> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_info_image_list_item, parent, false);
        return new MovieInfoImagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (images.get(position).bitmap != null) {
            final Bitmap poster = images.get(position).bitmap;
            holder.poster.setImageBitmap(poster);
        } else {
            loadImage(images.get(position).path, holder.poster);
        }
    }

    private void loadImage(String path, ImageView targetView) {
        ImageLoader.putPoster(context, path, targetView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(List<TmdbImage> images) {
        this.images = images;
    }

    public List<TmdbImage> getImages() {
        return images;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView placeholder;
        public ImageView poster;

        public MyViewHolder(View itemView) {
            super(itemView);

            placeholder = (CardView)itemView.findViewById(R.id.movie_info_poster_placeholder);
            poster = (ImageView)itemView.findViewById(R.id.movie_info_poster);
        }
    }
}


