package ru.surf.course.movierecommendations.fragments;


import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import ru.surf.course.movierecommendations.GalleryActivity;
import ru.surf.course.movierecommendations.MainActivity;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.MovieCreditsListAdapter;
import ru.surf.course.movierecommendations.adapters.MovieInfoImagesAdapter;
import ru.surf.course.movierecommendations.models.Credit;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by andrew on 11/26/16.
 */

public class MovieInfoFragment extends Fragment implements GetMoviesTask.TaskCompletedListener {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "moveId";
    final static int DATA_TO_LOAD = 3;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private TextView title;
    private ExpandableTextView overview;
    private Button expandCollapseOverviewButton;
    private TextView releaseDate;
    private ImageView poster;
    private MovieInfo currentMovie;
    private MovieInfo currentMovieEnglish;
    private FlowLayout genresPlaceholder;
    private TextView voteAverage;
    private RecyclerView imagesList;
    private RecyclerView creditsList;
    private Button photosButton;
    private ExpandableLinearLayout mediaPlaceholder;
    private TextView originalTitle;
    private TextView originalLanguage;
    private TextView budget;
    private TextView revenue;
    private TextView runtime;
    private TextView status;

    private MovieInfoImagesAdapter movieInfoImagesAdapter;
    private MovieCreditsListAdapter mMovieCreditsListAdapter;


    private int dataLoaded = 0;

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
        initViews(root);

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> poster = new ArrayList<String>();
                poster.add(currentMovie.getPosterPath());
                GalleryActivity.start(getActivity(), poster);
            }
        });

        photosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = ContextCompat.getColor(getActivity(), R.color.colorTextPrimary);
                int colorDark = ContextCompat.getColor(getActivity(), R.color.colorTextSecondary);
                photosButton.getBackground().setColorFilter(mediaPlaceholder.isExpanded() ? colorDark : color, PorterDuff.Mode.MULTIPLY);
                mediaPlaceholder.toggle();
            }
        });

        overview.setInterpolator(new OvershootInterpolator());

        View.OnClickListener expandCollapse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overview.toggle();
                expandCollapseOverviewButton.setBackground(overview.isExpanded() ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
            }
        };
        expandCollapseOverviewButton.setOnClickListener(expandCollapse);
        overview.setOnClickListener(expandCollapse);

        return root;
    }

    private void initViews(View root){
        progressBar = (ProgressBar) root.findViewById(R.id.movie_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
        title = (TextView) root.findViewById(R.id.movie_info_name);
        poster = (ImageView) root.findViewById(R.id.movie_info_poster);
        imagesList = (RecyclerView)root.findViewById(R.id.movie_info_images_list);
        imagesList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        creditsList = (RecyclerView)root.findViewById(R.id.movie_info_credits);
        creditsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        overview = (ExpandableTextView) root.findViewById(R.id.movie_info_overview);
        expandCollapseOverviewButton = (Button)root.findViewById(R.id.movie_info_button_expand_overview);
        releaseDate = (TextView) root.findViewById(R.id.movie_info_release_date);
        genresPlaceholder = (FlowLayout) root.findViewById(R.id.movie_info_genres_placeholder);
        voteAverage = (TextView) root.findViewById(R.id.movie_info_vote_average);
        photosButton = (Button)root.findViewById(R.id.movie_info_photos_btn);
        photosButton.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorTextSecondary), PorterDuff.Mode.MULTIPLY);
        mediaPlaceholder = (ExpandableLinearLayout) root.findViewById(R.id.movie_info_media_placeholder);
        originalTitle = (TextView)root.findViewById(R.id.movie_info_original_title);
        originalLanguage = (TextView)root.findViewById(R.id.movie_info_original_language);
        budget = (TextView)root.findViewById(R.id.movie_info_budget);
        revenue = (TextView)root.findViewById(R.id.movie_info_revenue);
        runtime = (TextView)root.findViewById(R.id.movie_info_runtime);
        status = (TextView)root.findViewById(R.id.movie_info_status);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int id = -1;
        if (getArguments().containsKey(KEY_MOVIE)) {
            id = ((MovieInfo) getArguments().getSerializable(KEY_MOVIE)).getId();
        } else if (getArguments().containsKey(KEY_MOVIE_ID)) {
            id = getArguments().getInt(KEY_MOVIE_ID);
        }
        dataLoaded = 0;
        loadInformation(id, getCurrentLocale().getLanguage());
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
        ImageLoader.getPoster(getActivity(), movieInfo.getPosterPath(), (int)getResources().getDimension(R.dimen.poster_width), (int)getResources().getDimension(R.dimen.poster_height), target, ImageLoader.sizes.w300);
    }

    private void loadBackdrops(final MovieInfo movie) {
        GetImagesTask getImagesTask = new GetImagesTask();
        getImagesTask.addListener(new GetImagesTask.TaskCompletedListener() {
            @Override
            public void getImagesTaskCompleted(List<TmdbImage> result) {
                movie.setBackdrops(result);
                movieInfoImagesAdapter = new MovieInfoImagesAdapter(result, getActivity());
                imagesList.setAdapter(movieInfoImagesAdapter);
                movieInfoImagesAdapter.setOnItemClickListener(new MovieInfoImagesAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        ArrayList<String> paths = new ArrayList<String>();
                        for (TmdbImage image:
                                movieInfoImagesAdapter.getImages()) {
                            paths.add(image.path);
                        }
                        GalleryActivity.start(getActivity(), paths, position);
                    }
                });
            }
        });
        getImagesTask.getMovieImages(movie.getId(), Tasks.GET_BACKDROPS);
    }

    private void loadCredits(int movieId) {
        GetCreditsTask getCreditsTask = new GetCreditsTask();
        getCreditsTask.addListener(new GetCreditsTask.CreditsTaskCompleteListener() {
            @Override
            public void taskCompleted(List<Credit> result) {
                currentMovie.setCredits(result);
                dataLoadComplete();
            }
        });
        getCreditsTask.getMovieCredits(movieId);
    }

    private void posterLoaded(Bitmap bitmap, MovieInfo movieInfo) {
        movieInfo.setPosterBitmap(bitmap);
    }

    private boolean checkInformation(MovieInfo movie) {
        if (movie.getOverview().equals("") || movie.getOverview().equals("null"))
            return false;
        return true;
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }


    public void fillInformation() {
        poster.setImageBitmap(currentMovie.getPosterBitmap());
        title.setText(currentMovie.getTitle());
        originalTitle.setText(currentMovie.getOriginalTitle());
        originalLanguage.setText(firstLetterToUpper(currentMovie.getOriginalLanguage().getDisplayLanguage()));
        if (!currentMovie.getBudget().equals("0"))
            budget.setText(currentMovie.getBudget() + "$");
        if (!currentMovie.getRevenue().equals("0"))
            revenue.setText(currentMovie.getRevenue() + "$");
        if (currentMovie.getRuntime() != 0)
            runtime.setText(currentMovie.getRuntime()/60 + getResources().getString(R.string.hours_short) + " " + currentMovie.getRuntime()%60 + getResources().getString(R.string.minutes_short));
        status.setText(currentMovie.getStatus());

        if (currentMovie.getOverview().equals("") || currentMovie.getOverview().equals("null"))
            overview.setText(currentMovieEnglish.getOverview());
        else overview.setText(currentMovie.getOverview());

        overview.post(new Runnable() {
            @Override
            public void run() {
                if (overview.getLineCount() >= overview.getMaxLines())
                    expandCollapseOverviewButton.setVisibility(View.VISIBLE);
            }
        });

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        if (currentMovie.getDate() != null) {
            releaseDate.setText("(" + dateFormat.format(currentMovie.getDate()) + ")");
        }
        for (String genreName : currentMovie.getGenreNames()) {
            Button genreButton = (Button) getActivity().getLayoutInflater().inflate(R.layout.genre_btn_template, null);
            genreButton.setText(genreName);
            genresPlaceholder.addView(genreButton);
            FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) genreButton.getLayoutParams();
            layoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.genre_button_margin_right), (int) getResources().getDimension(R.dimen.genre_button_margin_bottom));
            genreButton.setLayoutParams(layoutParams);
        }

        voteAverage.setText(String.valueOf(currentMovie.getVoteAverage()));
        if (currentMovie.getVoteAverage() >= 5)
            voteAverage.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRatingPositive));
        else
            voteAverage.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRatingNegative));

        mMovieCreditsListAdapter = new MovieCreditsListAdapter(currentMovie.getCredits(), getActivity());
        creditsList.setAdapter(mMovieCreditsListAdapter);
        mMovieCreditsListAdapter.setOnItemClickListener(new MovieCreditsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                PersonInfoFragment personInfoFragment = PersonInfoFragment.newInstance(mMovieCreditsListAdapter.getCredits().get(position).getPerson());
                if (getActivity() instanceof MainActivity){
                    ((MainActivity)getActivity()).switchContent(R.id.activity_main_container, personInfoFragment);
                }
            }
        });
    }

    @Override
    public void moviesLoaded(List<MovieInfo> result, boolean f) {
        if (result.get(0).getInfoLanguage().getLanguage().equals(getCurrentLocale().getLanguage())) {
            currentMovie = result.get(0);
            dataLoadComplete();
            loadPoster(currentMovie);
        }
        else if (result.get(0).getInfoLanguage().getLanguage().equals("en")){
            currentMovieEnglish = result.get(0);
            dataLoadComplete();
            loadPoster(currentMovieEnglish);
        }

        loadCredits(currentMovie.getId());
    }

    public void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentMovie) && currentMovieEnglish == null) {
                dataLoaded = 0;
                loadInformation(currentMovie.getId(), "en");
            }
            else {
                fillInformation();
                loadBackdrops(currentMovie);
                View progressBarPlaceholder = null;
                if (getView() != null)
                    progressBarPlaceholder = getView().findViewById(R.id.movie_info_progress_bar_placeholder);
                if (progressBarPlaceholder != null)
                    progressBarPlaceholder.setVisibility(View.GONE);
            }
        }
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    }

    public Locale getCurrentLocale() {
        return Locale.getDefault();
    }

}
