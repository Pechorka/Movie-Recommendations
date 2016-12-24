package ru.surf.course.movierecommendations.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.MovieInfo;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 11/26/16.
 */

public class MovieInfoFragment extends Fragment implements GetMoviesTask.TaskCompletedListener {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "moveId";
    final static int DATA_TO_LOAD = 2;
    final String LOG_TAG = getClass().getSimpleName();

    private TextView title;
    private TextView overview;
    private TextView releaseDate;
    private ImageView poster;
    private MovieInfo currentMovie;
    private MovieInfo currentMovieEnglish;
    private FlowLayout genresPlaceholder;
    private TextView voteAverage;

    private int dataLoaded = 0;

    private String language;

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
        if (getArguments() == null)
            onDestroy();
        View root = inflater.inflate(R.layout.fragment_movie_info, container, false);
        title = (TextView) root.findViewById(R.id.movie_info_name);
        poster = (ImageView) root.findViewById(R.id.movie_info_poster);
        overview = (TextView) root.findViewById(R.id.movie_info_overview);
        releaseDate = (TextView) root.findViewById(R.id.movie_info_release_date);
        genresPlaceholder = (FlowLayout) root.findViewById(R.id.movie_info_genres_placeholder);
        voteAverage = (TextView) root.findViewById(R.id.movie_info_vote_average);

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBigPoster();
            }
        });

        language = Locale.getDefault().getLanguage();

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int id = -1;
        if (getArguments().containsKey(KEY_MOVIE)) {
            id = ((MovieInfo) getArguments().getSerializable(KEY_MOVIE)).id;
        } else if (getArguments().containsKey(KEY_MOVIE_ID)) {
            id = getArguments().getInt(KEY_MOVIE_ID);
        }
        dataLoaded = 0;
        loadInformation(id, language);
    }

    public void showBigPoster() {
        //TODO make image popup somehow
    }


    public void loadInformation(int movieId, String language) {
        GetMoviesTask getMoviesTask = new GetMoviesTask();
        getMoviesTask.addListener(this);
        getMoviesTask.getMovieById(movieId, language);
    }

    public void loadPoster(final MovieInfo movieInfo) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.v(LOG_TAG, "poster loaded");
                posterLoaded(bitmap, movieInfo);
                dataLoadComplete();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                //TODO handle error
                Log.v(LOG_TAG, "poster load error");
                //repeat load
                loadPoster(movieInfo);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        ImageLoader.getPoster(getActivity(), movieInfo.posterPath, poster.getLayoutParams().width, poster.getLayoutParams().height, target);
    }

    private void posterLoaded(Bitmap bitmap, MovieInfo movieInfo) {
        movieInfo.posterBitmap = bitmap;
    }

    private boolean checkInformation(MovieInfo movie) {
        if (movie.overview.equals("") || movie.overview.equals("null"))
            return false;
        return true;
    }

    public void fillInformation() {
        poster.setImageBitmap(currentMovie.posterBitmap);
        title.setText(currentMovie.title);

        if (currentMovie.overview.equals("") || currentMovie.overview.equals("null"))
            overview.setText(currentMovieEnglish.overview);
        else overview.setText(currentMovie.overview);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        if (currentMovie.date != null) {
            releaseDate.setText("(" + dateFormat.format(currentMovie.date) + ")");
        }
        for (String genreName : currentMovie.genreNames) {
            Button genreButton = (Button) getActivity().getLayoutInflater().inflate(R.layout.genre_btn_template, null);
            genreButton.setText(genreName);
            genresPlaceholder.addView(genreButton);
            FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) genreButton.getLayoutParams();
            layoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.genre_button_margin_right), (int) getResources().getDimension(R.dimen.genre_button_margin_bottom));
            genreButton.setLayoutParams(layoutParams);
        }

        voteAverage.setText(String.valueOf(currentMovie.voteAverage));
        if (currentMovie.voteAverage >= 5)
            voteAverage.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRatingPositive));
        else
            voteAverage.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRatingNegative));

    }

    @Override
    public void taskCompleted(List<MovieInfo> result) {
        if (result.get(0).infoLanguage.getLanguage().equals(language)) {
            currentMovie = result.get(0);
            dataLoadComplete();
            loadPoster(currentMovie);
        }
        else if (result.get(0).infoLanguage.getLanguage().equals("en")){
            currentMovieEnglish = result.get(0);
            dataLoadComplete();
            loadPoster(currentMovieEnglish);
        }

    }

    public void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentMovie) && currentMovieEnglish == null) {
                dataLoaded = 0;
                loadInformation(currentMovie.id, "en");
            }
            else {
                fillInformation();
                View progressBarPlaceholder = null;
                if (getView() != null)
                    progressBarPlaceholder = getView().findViewById(R.id.movie_info_progress_bar_placeholder);
                if (progressBarPlaceholder != null)
                    progressBarPlaceholder.setVisibility(View.GONE);
            }
        }
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    }
}
