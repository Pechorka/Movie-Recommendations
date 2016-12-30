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

/**
 * Created by andrew on 12/30/16.
 */

public class MovieInfoImagesAdapter extends RecyclerView.Adapter<MovieInfoImagesAdapter.MyViewHolder> {

    private List<Bitmap> images;
    private Context context;

    public MovieInfoImagesAdapter(List<Bitmap> images, Context context) {
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
        final Bitmap poster = images.get(position);
        holder.poster.setImageBitmap(poster);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(List<Bitmap> images) {
        this.images = images;
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


