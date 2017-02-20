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
import android.widget.ProgressBar;

import java.util.Locale;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.TvShowSeasonsListAdapter;
import ru.surf.course.movierecommendations.models.TVShowInfo;


/**
 * Created by andrew on 2/20/17.
 */

public class TvShowSeasonsFragment extends Fragment {

    final static String KEY_TV = "tv";
    final static String KEY_TV_ID = "tv_id";
    final static int DATA_TO_LOAD = 1;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private TVShowInfo currentTv;
    private RecyclerView seasonsList;
    private TvShowSeasonsListAdapter tvShowSeasonsListAdapter;

    private int dataLoaded = 0;

    public static TvShowSeasonsFragment newInstance(TVShowInfo tv) {
        TvShowSeasonsFragment tvShowSeasonsFragment = new TvShowSeasonsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TV, tv);
        tvShowSeasonsFragment.setArguments(bundle);
        return tvShowSeasonsFragment;
    }

    public static TvShowSeasonsFragment newInstance(int tvId) {
        TvShowSeasonsFragment tvShowSeasonsFragment = new TvShowSeasonsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TV, tvId);
        tvShowSeasonsFragment.setArguments(bundle);
        return tvShowSeasonsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_tv_show_seasons, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.fragment_tv_show_seasons_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }

        seasonsList = (RecyclerView) root.findViewById(R.id.fragment_tv_show_seasons_list);
    }

    private void setupViews(View root) {
        seasonsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dataLoaded = 0;
        if (getArguments().containsKey(KEY_TV)) {
            currentTv = (TVShowInfo) getArguments().getSerializable(KEY_TV);
        } else if (getArguments().containsKey(KEY_TV_ID)) {
            currentTv = new TVShowInfo(getArguments().getInt(KEY_TV_ID));
        }
        loadReviews(currentTv);
    }

    private void loadReviews(final TVShowInfo tv) {
        //load seasons
        dataLoadComplete();
    }

    private void fillInformation() {
        if (currentTv.getSeasonList() != null && currentTv.getSeasonList().size() != 0) {
            tvShowSeasonsListAdapter = new TvShowSeasonsListAdapter(getActivity(), currentTv.getSeasonList());
            seasonsList.setAdapter(tvShowSeasonsListAdapter);
        } else {
            getView().findViewById(R.id.fragment_tv_show_seasons_no_seasons_message_placeholder).setVisibility(View.VISIBLE);
        }
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            fillInformation();
            View progressBarPlaceholder = null;
            if (getView() != null)
                progressBarPlaceholder = getView().findViewById(R.id.fragment_tv_show_seasons_progress_bar_placeholder);
            if (progressBarPlaceholder != null)
                progressBarPlaceholder.setVisibility(View.GONE);

        }

    }

    private Locale getCurrentLocale() {
        return Locale.getDefault();
    }
    
}
