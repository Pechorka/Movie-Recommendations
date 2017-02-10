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
import ru.surf.course.movierecommendations.fragments.MovieInfoFragment;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.TVShowInfo;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by Sergey on 09.02.2017.
 */

public class GridTVShowsAdapter extends RecyclerView.Adapter<GridViewHolder> {

    private List<TVShowInfo> tvShowInfoList;
    private Context context;

    public GridTVShowsAdapter(Context context, List<TVShowInfo> tvShowInfoList) {
        this.context = context;
        this.tvShowInfoList = tvShowInfoList;
    }


    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
        return new GridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {

        TVShowInfo tvShowInfo = tvShowInfoList.get(position);
        ImageLoader.putPoster(context, tvShowInfo.getPosterPath(), holder.image, ImageLoader.sizes.w300);

    }


    @Override
    public int getItemCount() {
        return tvShowInfoList.size();
    }

    private void fragmentToSwitch(MovieInfo info) {
        MovieInfoFragment movieInfoFragment = MovieInfoFragment.newInstance(info);
        switchContent(R.id.activity_main_container, movieInfoFragment);
    }

    public void setTvShowInfoList(List<TVShowInfo> list) {
        tvShowInfoList = list;
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
