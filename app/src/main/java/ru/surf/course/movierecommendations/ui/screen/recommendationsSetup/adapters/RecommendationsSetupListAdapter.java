package ru.surf.course.movierecommendations.ui.screen.recommendationsSetup.adapters;

import android.content.Context;
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
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;


public class RecommendationsSetupListAdapter extends
        RecyclerView.Adapter<RecommendationsSetupListAdapter.RecommendationsViewHolder> {

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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommendations_setup_list_element, parent, false);
        return new RecommendationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecommendationsViewHolder holder, int position) {
        Media media = mediaList.get(position);
        ImageLoader.putPoster(context, media.getPosterPath(), holder.poster, ImageLoader.sizes.w500);
        if (choosen[position])
            holder.choosen.setVisibility(View.VISIBLE);
        else
            holder.choosen.setVisibility(View.INVISIBLE);
        holder.cardView.setOnClickListener(v -> {
            choosen[holder.getAdapterPosition()] = !choosen[holder.getAdapterPosition()];
            // TODO fix
            if (choosen[holder.getAdapterPosition()]) {
                holder.choosen.setVisibility(View.VISIBLE);
            } else {
                holder.choosen.setVisibility(View.INVISIBLE);
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
                result.addAll(mediaList.get(i).getGenresIds());
            }
        }
        return result;
    }

    public static class RecommendationsViewHolder extends RecyclerView.ViewHolder {

        public ImageView poster;
        public CardView cardView;
        public ImageView choosen;

        public RecommendationsViewHolder(View itemView) {
            super(itemView);

            poster = (ImageView) itemView.findViewById(R.id.recommendations_setup_list_element_image);
            choosen = (ImageView) itemView.findViewById(R.id.recommendations_setup_list_element_chosen);
            cardView = (CardView) itemView.findViewById(R.id.recommendations_setup_list_element_cardview);
        }
    }
}
