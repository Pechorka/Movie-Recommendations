package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;

/**
 * Created by andrew on 2/11/17.
 */

public class GenresListAdapter extends RecyclerView.Adapter<GenresListAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mGenres;
    private static OnItemClickListener listener;

    public GenresListAdapter(Context context, List<String> genres) {
        mContext = context;
        mGenres = genres;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.genres_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mButton.setText(mGenres.get(position));
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        Button mButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            mButton = (Button) itemView.findViewById(R.id.genres_list_item_button);

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        GenresListAdapter.listener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        GenresListAdapter.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
