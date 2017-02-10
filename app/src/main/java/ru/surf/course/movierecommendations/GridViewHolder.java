package ru.surf.course.movierecommendations;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sergey on 09.02.2017.
 */

public class GridViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public CardView cardView;

    public GridViewHolder(View itemView) {
        super(itemView);

        image = (ImageView) itemView.findViewById(R.id.grid_image);
        cardView = (CardView) itemView.findViewById(R.id.grid_cv);
    }
}
