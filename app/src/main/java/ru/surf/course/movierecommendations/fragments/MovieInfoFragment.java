package ru.surf.course.movierecommendations.fragments;

import android.graphics.PorterDuff;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.activities.GalleryActivity;
import ru.surf.course.movierecommendations.activities.MainActivity;
import ru.surf.course.movierecommendations.adapters.MovieCreditsListAdapter;
import ru.surf.course.movierecommendations.adapters.MovieInfoImagesAdapter;
import ru.surf.course.movierecommendations.models.Credit;
import ru.surf.course.movierecommendations.models.Media;
import ru.surf.course.movierecommendations.models.MovieInfo;
import ru.surf.course.movierecommendations.models.Person;
import ru.surf.course.movierecommendations.models.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMoviesTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by andrew on 2/11/17.
 */

public class MovieInfoFragment extends Fragment {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "movie_id";
    final static int DATA_TO_LOAD = 3;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private ExpandableTextView overview;
    private Button expandCollapseBiographyButton;
    private MovieInfo currentMovieInfo;
    private MovieInfo currentMovieInfoEnglish;
    private TextView voteAverage;
    private RecyclerView backdrops;
    private MovieInfoImagesAdapter movieInfoImagesAdapter;
    private RecyclerView credits;
    private MovieCreditsListAdapter movieCreditsListAdapter;
    private FlowLayout genres;
    private TextView runtime;
    private TextView status;
    private TextView budget;
    private TextView revenue;
    private TextView productionCountries;

    private int dataLoaded = 0;

    public static MovieInfoFragment newInstance(MovieInfo movie) {  //considering this object already has all info
        MovieInfoFragment movieFactsFragment = new MovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
    }

    public static MovieInfoFragment newInstance(int movieId) {
        MovieInfoFragment movieFactsFragment = new MovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_movie_info, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root){
        progressBar = (ProgressBar) root.findViewById(R.id.movie_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
        overview = (ExpandableTextView) root.findViewById(R.id.movie_info_overview);
        expandCollapseBiographyButton = (Button)root.findViewById(R.id.movie_info_biography_expand_btn);
        voteAverage = (TextView)root.findViewById(R.id.movie_info_vote_average);
        backdrops = (RecyclerView)root.findViewById(R.id.movie_info_images_list);
        credits = (RecyclerView)root.findViewById(R.id.movie_info_credits);
        budget = (TextView)root.findViewById(R.id.movie_info_budget);
        revenue = (TextView)root.findViewById(R.id.movie_info_revenue);
        runtime = (TextView)root.findViewById(R.id.movie_info_runtime);
        status = (TextView)root.findViewById(R.id.movie_info_status);
        productionCountries = (TextView)root.findViewById(R.id.movie_info_production);
        genres = (FlowLayout) root.findViewById(R.id.movie_info_genres_placeholder);
    }

    private void setupViews(View root) {
        overview.setInterpolator(new AccelerateDecelerateInterpolator());

        View.OnClickListener expandCollapse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overview.toggle();
                expandCollapseBiographyButton.setBackground(overview.isExpanded() ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
            }
        };
        expandCollapseBiographyButton.setOnClickListener(expandCollapse);
        overview.setOnClickListener(expandCollapse);
        backdrops.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        credits.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dataLoaded = 0;
        if (getArguments().containsKey(KEY_MOVIE)) {
            currentMovieInfo = (MovieInfo) getArguments().getSerializable(KEY_MOVIE);
            dataLoadComplete();
            loadBackdropsInto(currentMovieInfo);
            loadCreditsInto(currentMovieInfo);
        } else if (getArguments().containsKey(KEY_MOVIE_ID)) {
            currentMovieInfo = new MovieInfo(getArguments().getInt(KEY_MOVIE_ID));
            loadInformationInto(currentMovieInfo, getCurrentLocale().getLanguage());
        }
    }

    private void loadInformationInto(final MovieInfo movie, String language) {
        GetMoviesTask getMovieInfosTask = new GetMoviesTask();
        getMovieInfosTask.addListener(new GetMediaTask.TaskCompletedListener() {
            @Override
            public void mediaLoaded(List<? extends Media> result, boolean newResult) {
                if (movie != null)
                    movie.fillFields(result.get(0));
                dataLoadComplete();
                loadBackdropsInto(currentMovieInfo);
                loadCreditsInto(currentMovieInfo);
            }
        });
        getMovieInfosTask.getMediaById(movie.getId(), language);
    }

    private void loadBackdropsInto(final MovieInfo movie) {
        GetImagesTask getImagesTask = new GetImagesTask();
        getImagesTask.addListener(new GetImagesTask.TaskCompletedListener() {
            @Override
            public void getImagesTaskCompleted(List<TmdbImage> result) {
                movie.setBackdrops(result);
                dataLoadComplete();
            }
        });
        getImagesTask.getMovieImages(movie.getId(), Tasks.GET_BACKDROPS);
    }

    private void loadCreditsInto(final MovieInfo movieInfo) {
        GetCreditsTask getCreditsTask = new GetCreditsTask();
        getCreditsTask.addListener(new GetCreditsTask.CreditsTaskCompleteListener() {
            @Override
            public void taskCompleted(List<Credit> result) {
                movieInfo.setCredits(result);
                dataLoadComplete();
            }
        });
        getCreditsTask.getMovieCredits(movieInfo.getId());
    }

    private boolean checkInformation(MovieInfo movie) {
        return Utilities.checkString(movie.getOverview());
        //for now checking only overview
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }


    private void fillInformation() {

        if (Utilities.checkString(currentMovieInfo.getOverview()))
            overview.setText(currentMovieInfo.getOverview());
        else overview.setText(currentMovieInfoEnglish.getOverview());

        overview.post(new Runnable() {
            @Override
            public void run() {
                if (overview.getLineCount() >= overview.getMaxLines())
                    expandCollapseBiographyButton.setVisibility(View.VISIBLE);
            }
        });

        voteAverage.setText(String.valueOf(currentMovieInfo.getVoteAverage()));

        movieInfoImagesAdapter = new MovieInfoImagesAdapter(currentMovieInfo.getBackdrops(), getActivity());
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
        backdrops.setAdapter(movieInfoImagesAdapter);

        movieCreditsListAdapter = new MovieCreditsListAdapter(currentMovieInfo.getCredits(), getActivity());
        movieCreditsListAdapter.setOnItemClickListener(new MovieCreditsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (getActivity() instanceof MainActivity) {
                    Person person = movieCreditsListAdapter.getCredits().get(position).getPerson();
                    ((MainActivity) getActivity()).switchContent(R.id.activity_main_container, PersonFragment.newInstance(person));
                }
            }
        });
        credits.setAdapter(movieCreditsListAdapter);

        if (!currentMovieInfo.getBudget().equals("0"))
            budget.setText(currentMovieInfo.getBudget() + "$");

        if (!currentMovieInfo.getRevenue().equals("0"))
            revenue.setText(currentMovieInfo.getRevenue() + "$");

        if (currentMovieInfo.getRuntime() != 0)
            runtime.setText(currentMovieInfo.getRuntime()/60 + getResources().getString(R.string.hours_short) + " " + currentMovieInfo.getRuntime()%60 + getResources().getString(R.string.minutes_short));

        status.setText(currentMovieInfo.getStatus());

        if (currentMovieInfo.getProductionCountriesNames() != null && currentMovieInfo.getProductionCountriesNames().size() != 0) {
            ArrayList<String> productionCountriesList = new ArrayList<>(currentMovieInfo.getProductionCountriesNames());
            String string = "";
            for (int i = 0; i < productionCountriesList.size(); i++) {
                string += productionCountriesList.get(i);
                if (i < productionCountriesList.size() - 1)
                    string += ",";
            }
            productionCountries.setText(string);
        }

        for (String genreName : currentMovieInfo.getGenreNames()) {
            Button genreButton = (Button) getActivity().getLayoutInflater().inflate(R.layout.genre_btn_template, null);
            genreButton.setText(genreName);
            genres.addView(genreButton);
            FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) genreButton.getLayoutParams();
            layoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.genre_button_margin_right), (int) getResources().getDimension(R.dimen.genre_button_margin_bottom));
            genreButton.setLayoutParams(layoutParams);
        }
    }

    private void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentMovieInfo) && currentMovieInfoEnglish == null) {
                dataLoaded--;
                currentMovieInfoEnglish = new MovieInfo(currentMovieInfo.getId());
                loadInformationInto(currentMovieInfoEnglish, Locale.ENGLISH.getLanguage());
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

    private Locale getCurrentLocale() {
        return Locale.getDefault();
    }
    
}
