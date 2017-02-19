package ru.surf.course.movierecommendations.view_holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 12.02.2017.
 */

public class ListViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public ImageView image;
    public CardView cardView;
    public TextView averageRating;

    public ListViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.list_text);
        image = (ImageView) itemView.findViewById(R.id.list_image);
        cardView = (CardView) itemView.findViewById(R.id.list_cv);
        averageRating = (TextView) itemView.findViewById(R.id.list_average_rating);
    }
}
