package ru.surf.course.movierecommendations.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.surf.course.movierecommendations.GridViewHolder;
import ru.surf.course.movierecommendations.MainActivity;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.fragments.MovieFragment;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by sergey on 05.12.16.
 */

public class GridMoviesAdapter extends RecyclerView.Adapter<GridViewHolder> {

    private List<MovieInfo> movieInfoList;
    private Context context;

    public GridMoviesAdapter(Context context, List<MovieInfo> movieInfoList) {
        this.context = context;
        this.movieInfoList = movieInfoList;
    }



    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
        return new GridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        final MovieInfo movieInfo = movieInfoList.get(position);
        ImageLoader.putPoster(context, movieInfo.getPosterPath(), holder.image, ImageLoader.sizes.w500);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentToSwitch(movieInfo);
            }
        });
    }


    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }

    private void fragmentToSwitch(MovieInfo info) {
        MovieFragment movieInfoFragment = MovieFragment.newInstance(info);
        switchContent(R.id.activity_main_container, movieInfoFragment);
    }

    public void setMovieInfoList(List<MovieInfo> list) {
        movieInfoList = list;
    }


    private void switchContent(int id, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.switchContent(id, fragment);
        }

    }
}
