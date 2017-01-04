package ru.surf.course.movierecommendations.adapters;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import ru.surf.course.movierecommendations.MainActivity;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.fragments.MovieInfoFragment;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

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


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MovieInfo movieInfo = movieInfoList.get(position);
        holder.name.setText(movieInfo.title);
        loadPoster(movieInfo);
        holder.image.setImageBitmap(movieInfo.posterBitmap);
//        ImageLoader.putPoster(context, movieInfo.posterPath, holder.image);
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

    private void loadPoster(final MovieInfo movieInfo) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                posterLoaded(bitmap, movieInfo);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                //repeat load
                loadPoster(movieInfo);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        ImageLoader.getPoster(context, movieInfo.posterPath, (int) context.getResources().getDimension(R.dimen.poster_width), (int) context.getResources().getDimension(R.dimen.poster_height), target);
    }

    private void posterLoaded(Bitmap bitmap, MovieInfo movieInfo) {
        movieInfo.posterBitmap = bitmap;
    }

    private void fragmentToSwitch(MovieInfo info) {
        MovieInfoFragment movieInfoFragment = MovieInfoFragment.newInstance(info);
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
            mainActivity.switchContent(id, fragment,new int[]{R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left});
        }

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.list_text);
            image = (ImageView) itemView.findViewById(R.id.list_image);
            cardView = (CardView) itemView.findViewById(R.id.list_cv);
        }

    }
}
