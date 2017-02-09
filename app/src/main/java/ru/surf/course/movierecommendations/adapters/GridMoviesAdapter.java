package ru.surf.course.movierecommendations.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.surf.course.movierecommendations.MainActivity;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.fragments.MovieInfoFragment;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.TVShowInfo;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by sergey on 05.12.16.
 */

public class GridMoviesAdapter extends RecyclerView.Adapter<GridMoviesAdapter.MyViewHolder> {

    private List<MovieInfo> movieInfoList;
    private List<TVShowInfo> tvShowInfoList;
    private Context context;
    private ActionBarDrawerToggle toggle;
    private boolean movie;

    public GridMoviesAdapter(Context context, List<MovieInfo> movieInfoList, List<TVShowInfo> tvShowInfoList, ActionBarDrawerToggle drawerToggle) {
        this.context = context;
        this.movieInfoList = movieInfoList;
        this.tvShowInfoList = tvShowInfoList;
        toggle = drawerToggle;
        movie = true;
    }

    public GridMoviesAdapter(Context context, List<MovieInfo> movieInfoList, List<TVShowInfo> tvShowInfoList, ActionBarDrawerToggle drawerToggle, boolean movie) {
        this.context = context;
        this.movieInfoList = movieInfoList;
        this.tvShowInfoList = tvShowInfoList;
        this.movie = movie;
        toggle = drawerToggle;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (movie) {
            bind(movieInfoList.get(position), holder);
        } else {
            bind(tvShowInfoList.get(position), holder);
        }

    }

    private void bind(Object showOrMovie, MyViewHolder holder) {
        if (showOrMovie instanceof MovieInfo) {
            final MovieInfo movieInfo = (MovieInfo) showOrMovie;
            holder.name.setText(movieInfo.getTitle());
            ImageLoader.putPoster(context, movieInfo.getPosterPath(), holder.image, ImageLoader.sizes.w300);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentToSwitch(movieInfo);
                }
            });
        } else {
            TVShowInfo tvShowInfo = (TVShowInfo) showOrMovie;
            holder.name.setText(tvShowInfo.getTitle());
            ImageLoader.putPoster(context, tvShowInfo.getPosterPath(), holder.image, ImageLoader.sizes.w300);
        }
    }

    @Override
    public int getItemCount() {
        if (movie) {
            return movieInfoList.size();
        } else {
            return tvShowInfoList.size();
        }
    }

    private void fragmentToSwitch(MovieInfo info) {
        MovieInfoFragment movieInfoFragment = MovieInfoFragment.newInstance(info);
        switchContent(R.id.activity_main_container, movieInfoFragment);
    }

    public void setMovieInfoList(List<MovieInfo> list) {
        movieInfoList = list;
    }

    public void setTvShowInfoList(List<TVShowInfo> list) {
        tvShowInfoList = list;
    }

    public void switchContentType(boolean movie) {
        this.movie = movie;
    }

    private void switchContent(int id, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            toggle.setDrawerIndicatorEnabled(false);
            mainActivity.switchContent(id, fragment, new int[]{R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left});
        }

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.grid_text);
            image = (ImageView) itemView.findViewById(R.id.grid_image);
            cardView = (CardView) itemView.findViewById(R.id.grid_cv);
        }

    }
}
