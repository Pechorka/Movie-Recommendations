package ru.surf.course.movierecommendations.ui.screen.tvShowInfo;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.presenter.MvpPresenter;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import at.blogc.android.views.ExpandableTextView;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.ui.base.fragment.BaseFragmentView;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.adapters.CreditsOfPeopleListAdapter;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.adapters.ImagesListAdapter;
import ru.surf.course.movierecommendations.ui.screen.person.PersonActivityView;
import ru.surf.course.movierecommendations.ui.screen.tvShow.TvShowActivityView;
import ru.surf.course.movierecommendations.util.Utilities;


public class TvShowInfoFragmentView extends BaseFragmentView {

    final static String KEY_TV_SHOW = "tv";
    final static String KEY_TV_SHOW_ID = "movie_id";

    @Inject
    TvShowInfoFragmentPresenter presenter;

    private ExpandableTextView overview;
    private Button expandCollapseOverviewButton;
    private TextView voteAverage;
    private RecyclerView backdrops;
    private ImagesListAdapter mImagesListAdapter;
    private RecyclerView credits;
    private CreditsOfPeopleListAdapter mCreditsOfPeopleListAdapter;
    private FlowLayout genres;
    private TextView episodeRuntime;
    private TextView status;
    private TextView numberOfSeasons;

    public static TvShowInfoFragmentView newInstance(
            TVShowInfo tvShow) {  //considering this object already has all info
        TvShowInfoFragmentView movieFactsFragment = new TvShowInfoFragmentView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TV_SHOW, tvShow);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
    }

    public static TvShowInfoFragmentView newInstance(int movieId) {
        TvShowInfoFragmentView movieFactsFragment = new TvShowInfoFragmentView();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TV_SHOW_ID, movieId);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
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
        return DaggerTvShowInfoFragmentComponent.builder()
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

        View root = inflater.inflate(R.layout.fragment_tv_show_info, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.tv_show_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
        }
        overview = (ExpandableTextView) root.findViewById(R.id.tv_show_info_overview);
        expandCollapseOverviewButton = (Button) root
                .findViewById(R.id.tv_show_info_biography_expand_btn);
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

        View.OnClickListener expandCollapse = view -> {
            overview.toggle();
            expandCollapseOverviewButton.setBackground(overview.isExpanded() ? ContextCompat
                    .getDrawable(getActivity(), R.drawable.ic_arrow_down)
                    : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
        };
        expandCollapseOverviewButton.setOnClickListener(expandCollapse);
        overview.setOnClickListener(expandCollapse);
        backdrops.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        credits.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    void fillInformation(TVShowInfo data, TVShowInfo fallbackData) {

        if (Utilities.checkString(data.getOverview())) {
            overview.setText(data.getOverview());
        } else {
            overview.setText(fallbackData.getOverview());
        }

        overview.post(() -> {
            if (overview.getLineCount() >= overview.getMaxLines()) {
                expandCollapseOverviewButton.setVisibility(View.VISIBLE);
            }
        });

        voteAverage.setText(String.valueOf(data.getVoteAverage()));

        status.setText(data.getStatus());

        episodeRuntime.setText(String.valueOf(data.getEpisodesRuntime().get(0)));

        numberOfSeasons.setText(String.valueOf(data.getNumberOfSeasons()));

        mImagesListAdapter = new ImagesListAdapter(data.getBackdrops(), getActivity());
        mImagesListAdapter.setOnItemClickListener(position -> {
            ArrayList<String> paths = new ArrayList<>();
            for (TmdbImage image :
                    mImagesListAdapter.getImages()) {
                paths.add(image.path);
            }
            GalleryActivityView.start(getActivity(), paths, position);
        });
        backdrops.setAdapter(mImagesListAdapter);

        mCreditsOfPeopleListAdapter = new CreditsOfPeopleListAdapter(data.getCredits(),
                getActivity());
        mCreditsOfPeopleListAdapter
                .setOnItemClickListener(position -> PersonActivityView.start(getActivity(),
                        mCreditsOfPeopleListAdapter.getCredits().get(position).getPerson()));
        credits.setAdapter(mCreditsOfPeopleListAdapter);

        for (final Genre genre : data.getGenres()) {
            Button genreButton = (Button) getActivity().getLayoutInflater()
                    .inflate(R.layout.genre_btn_template, null);
            genreButton.setText(genre.getName());
            genres.addView(genreButton);
            FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) genreButton
                    .getLayoutParams();
            layoutParams
                    .setMargins(0, 0, (int) getResources().getDimension(R.dimen.genre_button_margin_right),
                            (int) getResources().getDimension(R.dimen.genre_button_margin_bottom));
            genreButton.setLayoutParams(layoutParams);

            genreButton.setOnClickListener(view -> {
                if (getActivity() instanceof TvShowActivityView) {
                    ((TvShowActivityView) getActivity()).onGenreClick(genre);
                }
            });
        }
    }

    void hideProgressBar() {
        View progressBarPlaceholder = null;
        if (getView() != null) {
            progressBarPlaceholder = getView()
                    .findViewById(R.id.tv_show_info_progress_bar_placeholder);
        }
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

}
