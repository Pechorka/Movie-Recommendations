package ru.surf.course.movierecommendations.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 30.03.2017.
 */

public class RecommendationsSetupListAdapter extends RecyclerView.Adapter<RecommendationsSetupListAdapter.RecommendationsViewHolder> {

    @Override
    public RecommendationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecommendationsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class RecommendationsViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public RecommendationsViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.recommendations_setup_list_element_image);
        }
    }
}
