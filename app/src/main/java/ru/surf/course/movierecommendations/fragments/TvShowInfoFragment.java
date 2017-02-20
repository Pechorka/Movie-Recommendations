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
import ru.surf.course.movierecommendations.activities.PersonActivity;
import ru.surf.course.movierecommendations.activities.TvShowActivity;
import ru.surf.course.movierecommendations.adapters.MovieCreditsListAdapter;
import ru.surf.course.movierecommendations.adapters.MovieInfoImagesAdapter;
import ru.surf.course.movierecommendations.models.Credit;
import ru.surf.course.movierecommendations.models.Genre;
import ru.surf.course.movierecommendations.models.TVShowInfo;
import ru.surf.course.movierecommendations.models.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetMediaTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetTVShowsTask;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by andrew on 2/19/17.
 */

public class TvShowInfoFragment extends Fragment {

    final static String KEY_TV_SHOW = "tvShow";
    final static String KEY_TV_SHOW_ID = "movie_id";
    final static int DATA_TO_LOAD = 3;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private ExpandableTextView overview;
    private Button expandCollapseOverviewButton;
    private TVShowInfo currentTvShowInfo;
    private TVShowInfo currentTvShowInfoEnglish;
    private TextView voteAverage;
    private RecyclerView backdrops;
    private MovieInfoImagesAdapter movieInfoImagesAdapter;
    private RecyclerView credits;
    private MovieCreditsListAdapter movieCreditsListAdapter;
    private FlowLayout genres;
    private TextView episodeRuntime;
    private TextView status;
    private TextView numberOfSeasons;

    private int dataLoaded = 0;

    public static TvShowInfoFragment newInstance(TVShowInfo tvShow) {  //considering this object already has all info
        TvShowInfoFragment movieFactsFragment = new TvShowInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TV_SHOW, tvShow);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
    }

    public static TvShowInfoFragment newInstance(int movieId) {
        TvShowInfoFragment movieFactsFragment = new TvShowInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TV_SHOW_ID, movieId);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_tv_show_info, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.tv_show_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
        overview = (ExpandableTextView) root.findViewById(R.id.tv_show_info_overview);
        expandCollapseOverviewButton = (Button) root.findViewById(R.id.tv_show_info_biography_expand_btn);
        voteAverage = (TextView) root.findViewById(R.id.tv_show_info_vote_average);
        backdrops = (RecyclerView) root.findViewById(R.id.tv_show_info_images_list);
        credits = (RecyclerView) root.findViewById(R.id.tv_show_info_credits);
        episodeRuntime = (TextView) root.findViewById(R.id.tv_show_info_episode_runtime);
        status = (TextView) root.findViewById(R.id.tv_show_info_status);
        numberOfSeasons = (TextView) root.findViewById(R.id.tv_show_info_number_of_seasons);
        genres = (FlowLayout) root.findViewById(R.id.tv_show_info_genres_placeholder);
    }

    private void setupViews(View root) {
        overview.setInterpolator(new AccelerateDecelerateInterpolator());

        View.OnClickListener expandCollapse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overview.toggle();
                expandCollapseOverviewButton.setBackground(overview.isExpanded() ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
            }
        };
        expandCollapseOverviewButton.setOnClickListener(expandCollapse);
        overview.setOnClickListener(expandCollapse);
        backdrops.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        credits.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dataLoaded = 0;
        if (getArguments().containsKey(KEY_TV_SHOW)) {
            currentTvShowInfo = (TVShowInfo) getArguments().getSerializable(KEY_TV_SHOW);
            dataLoadComplete();
            loadBackdropsInto(currentTvShowInfo);
            loadCreditsInto(currentTvShowInfo);
        } else if (getArguments().containsKey(KEY_TV_SHOW_ID)) {
            currentTvShowInfo = new TVShowInfo(getArguments().getInt(KEY_TV_SHOW_ID));
            loadInformationInto(currentTvShowInfo, getCurrentLocale().getLanguage());
        }
    }

    private void loadInformationInto(final TVShowInfo tvShow, String language) {
        GetTVShowsTask getTVShowsTask = new GetTVShowsTask();
        getTVShowsTask.addListener(new GetMediaTask.TaskCompletedListener<TVShowInfo>() {
            @Override
            public void mediaLoaded(List<TVShowInfo> result, boolean newResult) {
                if (tvShow != null)
                    tvShow.fillFields(result.get(0));
                dataLoadComplete();
                loadBackdropsInto(currentTvShowInfo);
                loadCreditsInto(currentTvShowInfo);
            }
        });
        getTVShowsTask.getMediaById(tvShow.getId(), language);
    }

    private void loadBackdropsInto(final TVShowInfo tvShow) {
        GetImagesTask getImagesTask = new GetImagesTask();
        getImagesTask.addListener(new GetImagesTask.TaskCompletedListener() {
            @Override
            public void getImagesTaskCompleted(List<TmdbImage> result) {
                tvShow.setBackdrops(result);
                dataLoadComplete();
            }
        });
        getImagesTask.getTvImages(tvShow.getId(), Tasks.GET_TV_BACKDROPS);
    }

    private void loadCreditsInto(final TVShowInfo tvShowInfo) {
        GetCreditsTask getCreditsTask = new GetCreditsTask();
        getCreditsTask.addListener(new GetCreditsTask.CreditsTaskCompleteListener() {
            @Override
            public void taskCompleted(List<Credit> result) {
                tvShowInfo.setCredits(result);
                dataLoadComplete();
            }
        });
        getCreditsTask.getTVShowCredits(tvShowInfo.getId());
    }

    private boolean checkInformation(TVShowInfo tvShow) {
        return Utilities.checkString(tvShow.getOverview());
        //for now checking only overview
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    private void fillInformation() {

        if (Utilities.checkString(currentTvShowInfo.getOverview()))
            overview.setText(currentTvShowInfo.getOverview());
        else overview.setText(currentTvShowInfoEnglish.getOverview());

        overview.post(new Runnable() {
            @Override
            public void run() {
                if (overview.getLineCount() >= overview.getMaxLines())
                    expandCollapseOverviewButton.setVisibility(View.VISIBLE);
            }
        });

        voteAverage.setText(String.valueOf(currentTvShowInfo.getVoteAverage()));

        status.setText(currentTvShowInfo.getStatus());

        episodeRuntime.setText(String.valueOf(currentTvShowInfo.getEpisodesRuntime().get(0)));

        numberOfSeasons.setText(String.valueOf(currentTvShowInfo.getNumberOfSeasons()));

        movieInfoImagesAdapter = new MovieInfoImagesAdapter(currentTvShowInfo.getBackdrops(), getActivity());
        movieInfoImagesAdapter.setOnItemClickListener(new MovieInfoImagesAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                ArrayList<String> paths = new ArrayList<String>();
                for (TmdbImage image :
                        movieInfoImagesAdapter.getImages()) {
                    paths.add(image.path);
                }
                GalleryActivity.start(getActivity(), paths, position);
            }
        });
        backdrops.setAdapter(movieInfoImagesAdapter);


        movieCreditsListAdapter = new MovieCreditsListAdapter(currentTvShowInfo.getCredits(), getActivity());
        movieCreditsListAdapter.setOnItemClickListener(new MovieCreditsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                PersonActivity.start(getActivity(), movieCreditsListAdapter.getCredits().get(position).getPerson());
            }
        });
        credits.setAdapter(movieCreditsListAdapter);


        for (final Genre genre : currentTvShowInfo.getGenres()) {
            Button genreButton = (Button) getActivity().getLayoutInflater().inflate(R.layout.genre_btn_template, null);
            genreButton.setText(genre.getName());
            genres.addView(genreButton);
            FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) genreButton.getLayoutParams();
            layoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.genre_button_margin_right), (int) getResources().getDimension(R.dimen.genre_button_margin_bottom));
            genreButton.setLayoutParams(layoutParams);

            genreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() instanceof TvShowActivity)
                        ((TvShowActivity)getActivity()).onGenreClick(genre);
                }
            });
        }
    }

    private void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentTvShowInfo) && currentTvShowInfoEnglish == null) {
                dataLoaded--;
                currentTvShowInfoEnglish = new TVShowInfo(currentTvShowInfo.getId());
                loadInformationInto(currentTvShowInfoEnglish, Locale.ENGLISH.getLanguage());
            } else {
                fillInformation();
                View progressBarPlaceholder = null;
                if (getView() != null)
                    progressBarPlaceholder = getView().findViewById(R.id.tv_show_info_progress_bar_placeholder);
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
