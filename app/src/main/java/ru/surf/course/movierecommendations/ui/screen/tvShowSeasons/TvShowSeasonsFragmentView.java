package ru.surf.course.movierecommendations.ui.screen.tvShowSeasons;

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

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.presenter.MvpPresenter;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.ui.base.fragment.BaseFragmentView;
import ru.surf.course.movierecommendations.ui.screen.tvShowSeasons.adapters.TvShowSeasonsListAdapter;


public class TvShowSeasonsFragmentView extends BaseFragmentView {

    final static String KEY_TV = "tv";
    final static String KEY_TV_ID = "tv_id";
    
    @Inject
    TvShowSeasonsFragmentPresenter presenter;

    private ProgressBar progressBar;
    private RecyclerView seasonsList;
    private TvShowSeasonsListAdapter tvShowSeasonsListAdapter;

    public static TvShowSeasonsFragmentView newInstance(TVShowInfo tv) {
        TvShowSeasonsFragmentView tvShowSeasonsFragmentView = new TvShowSeasonsFragmentView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TV, tv);
        tvShowSeasonsFragmentView.setArguments(bundle);
        return tvShowSeasonsFragmentView;
    }

    public static TvShowSeasonsFragmentView newInstance(int tvId) {
        TvShowSeasonsFragmentView tvShowSeasonsFragmentView = new TvShowSeasonsFragmentView();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TV, tvId);
        tvShowSeasonsFragmentView.setArguments(bundle);
        return tvShowSeasonsFragmentView;
    }

    @Override
    public MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerTvShowSeasonsFragmentComponent.builder()
                .fragmentModule(getFragmentModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() == null) {
            onDestroy();
        }

        View root = inflater.inflate(R.layout.fragment_tv_show_seasons, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.fragment_tv_show_seasons_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
        }

        seasonsList = (RecyclerView) root.findViewById(R.id.fragment_tv_show_seasons_list);
    }

    private void setupViews(View root) {
        seasonsList.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    void fillInformation(TVShowInfo data) {
        if (data.getSeasonList() != null && data.getSeasonList().size() != 0) {
            tvShowSeasonsListAdapter = new TvShowSeasonsListAdapter(getActivity(),
                    data.getSeasonList());
            seasonsList.setAdapter(tvShowSeasonsListAdapter);
        } else {
            getView().findViewById(R.id.fragment_tv_show_seasons_no_seasons_message_placeholder)
                    .setVisibility(View.VISIBLE);
        }
    }
    
    void hideProgressBar() {
        View progressBarPlaceholder = null;
        if (getView() != null) {
            progressBarPlaceholder = getView()
                    .findViewById(R.id.fragment_tv_show_seasons_progress_bar_placeholder);
        }
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }
}
