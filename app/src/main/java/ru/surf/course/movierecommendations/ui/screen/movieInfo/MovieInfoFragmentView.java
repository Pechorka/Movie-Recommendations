package ru.surf.course.movierecommendations.ui.screen.movieInfo;

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

import at.blogc.android.views.ExpandableTextView;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.presenter.MvpPresenter;

import java.util.ArrayList;

import org.apmem.tools.layouts.FlowLayout;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.ProductionCountries;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.ui.base.fragment.BaseFragmentView;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView;
import ru.surf.course.movierecommendations.ui.screen.movie.MovieActivityView;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.adapters.CreditsOfPeopleListAdapter;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.adapters.ImagesListAdapter;
import ru.surf.course.movierecommendations.ui.screen.person.PersonActivityView;
import ru.surf.course.movierecommendations.util.Utilities;


public class MovieInfoFragmentView extends BaseFragmentView {

    final static String KEY_MOVIE = "movie";
    final static String KEY_MOVIE_ID = "movie_id";
    final String LOG_TAG = getClass().getSimpleName();

    @Inject
    MovieInfoFragmentPresenter presenter;

    private ProgressBar progressBar;
    private ExpandableTextView overview;
    private Button expandCollapseBiographyButton;
    private TextView voteAverage;
    private RecyclerView backdrops;
    private ImagesListAdapter mImagesListAdapter;
    private RecyclerView credits;
    private CreditsOfPeopleListAdapter mCreditsOfPeopleListAdapter;
    private FlowLayout genres;
    private TextView runtime;
    private TextView status;
    private TextView budget;
    private TextView revenue;
    private TextView productionCountries;
    

    public static MovieInfoFragmentView newInstance(
            MovieInfo movie) {  //considering this object already has all info
        MovieInfoFragmentView movieFactsFragment = new MovieInfoFragmentView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
    }

    public static MovieInfoFragmentView newInstance(int movieId) {
        MovieInfoFragmentView movieFactsFragment = new MovieInfoFragmentView();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        movieFactsFragment.setArguments(bundle);
        return movieFactsFragment;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerMovieInfoFragmentComponent.builder()
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

        View root = inflater.inflate(R.layout.fragment_movie_info, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.movie_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
        }
        overview = (ExpandableTextView) root.findViewById(R.id.movie_info_overview);
        expandCollapseBiographyButton = (Button) root
                .findViewById(R.id.movie_info_biography_expand_btn);
        voteAverage = (TextView) root.findViewById(R.id.movie_info_vote_average);
        backdrops = (RecyclerView) root.findViewById(R.id.movie_info_images_list);
        credits = (RecyclerView) root.findViewById(R.id.movie_info_credits);
        budget = (TextView) root.findViewById(R.id.movie_info_budget);
        revenue = (TextView) root.findViewById(R.id.movie_info_revenue);
        runtime = (TextView) root.findViewById(R.id.movie_info_runtime);
        status = (TextView) root.findViewById(R.id.movie_info_status);
        productionCountries = (TextView) root.findViewById(R.id.movie_info_production);
        genres = (FlowLayout) root.findViewById(R.id.movie_info_genres_placeholder);
        
    }

    private void setupViews(View root) {
        overview.setInterpolator(new AccelerateDecelerateInterpolator());

        View.OnClickListener expandCollapse = v -> {
                overview.toggle();
                expandCollapseBiographyButton.setBackground(overview.isExpanded() ? ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_arrow_down)
                        : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
            };
        expandCollapseBiographyButton.setOnClickListener(expandCollapse);
        overview.setOnClickListener(expandCollapse);
        backdrops.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        credits.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    void fillInformation(MovieInfo data, MovieInfo fallbackData) {

        if (Utilities.checkString(data.getOverview())) {
            overview.setText(data.getOverview());
        } else {
            overview.setText(fallbackData.getOverview());
        }

        overview.post(() -> {
            if (overview.getLineCount() >= overview.getMaxLines()) {
                expandCollapseBiographyButton.setVisibility(View.VISIBLE);
            }
        });

        voteAverage.setText(String.valueOf(data.getVoteAverage()));

        mImagesListAdapter = new ImagesListAdapter(data.getBackdrops(), getActivity());
        mImagesListAdapter.setOnItemClickListener(position -> {
            ArrayList<String> paths = new ArrayList<String>();
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

        if (!data.getBudget().equals("0")) {
            budget.setText(data.getBudget() + "$");
        }

        if (!data.getRevenue().equals("0")) {
            revenue.setText(data.getRevenue() + "$");
        }

        if (data.getRuntime() != 0) {
            runtime.setText(
                    data.getRuntime() / 60 + getResources().getString(R.string.hours_short) + " "
                            + data.getRuntime() % 60 + getResources()
                            .getString(R.string.minutes_short));
        }

        status.setText(data.getStatus());

        if (data.getProductionCountries() != null
                && data.getProductionCountries().size() != 0) {
            ArrayList<ProductionCountries> productionCountriesList = new ArrayList<>(
                    data.getProductionCountries());
            String string = "";
            for (int i = 0; i < productionCountriesList.size(); i++) {
                string += productionCountriesList.get(i).getName();
                if (i < productionCountriesList.size() - 1) {
                    string += ",";
                }
            }
            productionCountries.setText(string);
        }

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
                if (getActivity() instanceof MovieActivityView) {
                    ((MovieActivityView) getActivity()).onGenreClick(genre);
                }
            });
        }
    }

    void hideProgressBar() {
        View progressBarPlaceholder = null;
        if (getView() != null) {
            progressBarPlaceholder = getView().findViewById(R.id.movie_info_progress_bar_placeholder);
        }
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

}
