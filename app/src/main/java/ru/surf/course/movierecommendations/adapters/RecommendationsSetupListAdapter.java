package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by Sergey on 30.03.2017.
 */

public class RecommendationsSetupListAdapter extends RecyclerView.Adapter<RecommendationsSetupListAdapter.RecommendationsViewHolder> {

    private List<Media> mediaList;
    private Context context;
    private boolean[] choosen;

    public RecommendationsSetupListAdapter(Context context, List<Media> mediaList) {
        this.mediaList = mediaList;
        this.context = context;
        choosen = new boolean[mediaList.size()];
    }

    @Override
    public RecommendationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendations_setup_list_element, parent, false);
        return new RecommendationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecommendationsViewHolder holder, int position) {
        Media media = mediaList.get(position);
        ImageLoader.putPoster(context, media.getPosterPath(), holder.imageView, ImageLoader.sizes.w500);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosen[holder.getAdapterPosition()] = !choosen[holder.getAdapterPosition()];
                // TODO fix colors
                if (choosen[holder.getAdapterPosition()]) {
                    holder.cardView.setBackgroundColor(Color.BLUE);
                } else {
                    holder.cardView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryDark, null));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
        choosen = new boolean[mediaList.size()];
        notifyDataSetChanged();
    }

    public Set<Integer> getGenres() {
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < mediaList.size(); i++) {
            if (choosen[i]) {
                result.addAll(mediaList.get(i).getmGenresIds());
            }
        }
        return result;
    }

    public static class RecommendationsViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public CardView cardView;

        public RecommendationsViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.recommendations_setup_list_element_image);
            cardView = (CardView) itemView.findViewById(R.id.recommendations_setup_list_element_cardview);
        }
    }
}
