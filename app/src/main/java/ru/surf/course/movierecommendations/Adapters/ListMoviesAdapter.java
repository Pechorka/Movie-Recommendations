package ru.surf.course.movierecommendations.Adapters;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.surf.course.movierecommendations.MainActivity;
import ru.surf.course.movierecommendations.MovieInfo;
import ru.surf.course.movierecommendations.MovieInfoFragment;
import ru.surf.course.movierecommendations.R;

/**
 * Created by sergey on 05.12.16.
 */

public class ListMoviesAdapter extends RecyclerView.Adapter<ListMoviesAdapter.MyViewHolder> {

    private List<MovieInfo> movieInfoList;
    private Context context;

    public ListMoviesAdapter(Context context, List<MovieInfo> movieInfoList) {
        this.context = context;
        this.movieInfoList = movieInfoList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.list_text);
            image = (ImageView) itemView.findViewById(R.id.list_image);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MovieInfo movieInfo = movieInfoList.get(position);
        holder.name.setText(movieInfo.title);
        Picasso.with(context).
                load(movieInfo.id)
                .noFade().resize(250, 375)
                .centerCrop()
                .into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentToSwitch(346672);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }

    private void fragmentToSwitch(int id) {
        MovieInfoFragment movieInfoFragment = MovieInfoFragment.newInstance(id);
        switchContent(R.id.activity_main_container, movieInfoFragment);
    }

    public void switchContent(int id, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.switchContent(id, fragment);
        }

    }
}
