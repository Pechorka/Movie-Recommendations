package ru.surf.course.movierecommendations;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 11/26/16.
 */

public class MovieInfoFragment extends Fragment implements GetMoviesTask.TaskCompletedListener{

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "moveId";

    private TextView title;
    private ImageView poster;
    private MovieInfo currentMovie;

    public static MovieInfoFragment newInstance(MovieInfo movieInfo) {
        MovieInfoFragment movieInfoFragment = new MovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movieInfo);
        movieInfoFragment.setArguments(bundle);
        return movieInfoFragment;
    }

    public static MovieInfoFragment newInstance(int movieId) {
        MovieInfoFragment movieInfoFragment = new MovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        movieInfoFragment.setArguments(bundle);
        return movieInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_info, container, true);
        title = (TextView)root.findViewById(R.id.movie_info_name);
        poster = (ImageView)root.findViewById(R.id.movie_info_poster);

        if (getArguments() == null)
            onDestroy();
        if (getArguments().containsKey(KEY_MOVIE)) {
            currentMovie = (MovieInfo) getArguments().getSerializable(KEY_MOVIE);
            fillInformation();
        }
        else if (getArguments().containsKey(KEY_MOVIE_ID)){
            GetMoviesTask getMoviesTask = new GetMoviesTask();
            getMoviesTask.addListener(this);
            getMoviesTask.getMovieInfo(getArguments().getInt(KEY_MOVIE_ID), "en");
        }

        return root;
    }

    public void fillInformation() {
        ImageLoader.putPoster(getActivity(), currentMovie.posterPath, poster);
        title.setText(currentMovie.title);
    }


    @Override
    public void taskCompleted(List<MovieInfo> result) {
        currentMovie = result.get(0);
        fillInformation();
    }
}
