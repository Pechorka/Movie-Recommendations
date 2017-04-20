package ru.surf.course.movierecommendations.ui.screen.movie;

import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;

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
import at.blogc.android.views.ExpandableTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Locale;
import org.apmem.tools.layouts.FlowLayout;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.ProductionCountries;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.interactor.GetMovieTaskRetrofit;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Tasks;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView;
import ru.surf.course.movierecommendations.ui.screen.movie.adapters.CreditsOfPeopleListAdapter;
import ru.surf.course.movierecommendations.ui.screen.movie.adapters.ImagesListAdapter;
import ru.surf.course.movierecommendations.ui.screen.person.PersonActivity;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
  private ImagesListAdapter mImagesListAdapter;
  private RecyclerView credits;
  private CreditsOfPeopleListAdapter mCreditsOfPeopleListAdapter;
  private FlowLayout genres;
  private TextView runtime;
  private TextView status;
  private TextView budget;
  private TextView revenue;
  private TextView productionCountries;

  private int dataLoaded = 0;

  public static MovieInfoFragment newInstance(
      MovieInfo movie) {  //considering this object already has all info
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

    View.OnClickListener expandCollapse = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        overview.toggle();
        expandCollapseBiographyButton.setBackground(overview.isExpanded() ? ContextCompat
            .getDrawable(getActivity(), R.drawable.ic_arrow_down)
            : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
      }
    };
    expandCollapseBiographyButton.setOnClickListener(expandCollapse);
    overview.setOnClickListener(expandCollapse);
    backdrops.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    credits.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
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
      loadInformationInto(currentMovieInfo, Utilities.getSystemLanguage());
    }
  }

  private void loadInformationInto(final MovieInfo movie, String language) {
    RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory
        .createWithScheduler(Schedulers.io());
    Gson gson = new GsonBuilder().create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(rxAdapter)
        .build();
    GetMovieTaskRetrofit getTVShowTaskRetrofit = retrofit.create(GetMovieTaskRetrofit.class);
    Observable<MovieInfo> call = getTVShowTaskRetrofit.getMovieById(movie.getMediaId(),
        BuildConfig.TMDB_API_KEY, language);
    call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(movieInfo -> {
          movie.fillFields(movieInfo);
      dataLoadComplete();
      loadBackdropsInto(currentMovieInfo);
      loadCreditsInto(currentMovieInfo);
    });
//    GetMoviesTask getMovieInfosTask = new GetMoviesTask();
//    getMovieInfosTask.addListener((result, newResult) -> {
//      if (movie != null) {
//        movie.fillFields(result.get(0));
//      }
//      dataLoadComplete();
//      loadBackdropsInto(currentMovieInfo);
//      loadCreditsInto(currentMovieInfo);
//    });
//    getMovieInfosTask.getMediaById(movie.getMediaId(), language);
  }

  private void loadBackdropsInto(final MovieInfo movie) {
    GetImagesTask getImagesTask = new GetImagesTask();
    getImagesTask.addListener(result -> {
      movie.setBackdrops(result);
      dataLoadComplete();
    });
    getImagesTask.getMovieImages(movie.getMediaId(), Tasks.GET_BACKDROPS);
  }

  private void loadCreditsInto(final MovieInfo movieInfo) {
    GetCreditsTask getCreditsTask = new GetCreditsTask();
    getCreditsTask.addListener(result -> {
      movieInfo.setCredits(result);
      dataLoadComplete();
    });
    getCreditsTask.getMovieCredits(movieInfo.getMediaId());
  }

  private boolean checkInformation(MovieInfo movie) {
    return Utilities.checkString(movie.getOverview());
    //for now checking only overview
  }

  private String firstLetterToUpper(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }


  private void fillInformation() {

    if (Utilities.checkString(currentMovieInfo.getOverview())) {
      overview.setText(currentMovieInfo.getOverview());
    } else {
      overview.setText(currentMovieInfoEnglish.getOverview());
    }

    overview.post(() -> {
      if (overview.getLineCount() >= overview.getMaxLines()) {
        expandCollapseBiographyButton.setVisibility(View.VISIBLE);
      }
    });

    voteAverage.setText(String.valueOf(currentMovieInfo.getVoteAverage()));

    mImagesListAdapter = new ImagesListAdapter(currentMovieInfo.getBackdrops(), getActivity());
    mImagesListAdapter.setOnItemClickListener(position -> {
      ArrayList<String> paths = new ArrayList<String>();
      for (TmdbImage image :
          mImagesListAdapter.getImages()) {
        paths.add(image.path);
      }
      GalleryActivityView.start(getActivity(), paths, position);
    });
    backdrops.setAdapter(mImagesListAdapter);

    mCreditsOfPeopleListAdapter = new CreditsOfPeopleListAdapter(currentMovieInfo.getCredits(),
        getActivity());
    mCreditsOfPeopleListAdapter
        .setOnItemClickListener(position -> PersonActivity.start(getActivity(),
            mCreditsOfPeopleListAdapter.getCredits().get(position).getPerson()));
    credits.setAdapter(mCreditsOfPeopleListAdapter);

    if (!currentMovieInfo.getBudget().equals("0")) {
      budget.setText(currentMovieInfo.getBudget() + "$");
    }

    if (!currentMovieInfo.getRevenue().equals("0")) {
      revenue.setText(currentMovieInfo.getRevenue() + "$");
    }

    if (currentMovieInfo.getRuntime() != 0) {
      runtime.setText(
          currentMovieInfo.getRuntime() / 60 + getResources().getString(R.string.hours_short) + " "
              + currentMovieInfo.getRuntime() % 60 + getResources()
              .getString(R.string.minutes_short));
    }

    status.setText(currentMovieInfo.getStatus());

    if (currentMovieInfo.getProductionCountries() != null
        && currentMovieInfo.getProductionCountries().size() != 0) {
      ArrayList<ProductionCountries> productionCountriesList = new ArrayList<>(
          currentMovieInfo.getProductionCountries());
      String string = "";
      for (int i = 0; i < productionCountriesList.size(); i++) {
        string += productionCountriesList.get(i).getName();
        if (i < productionCountriesList.size() - 1) {
          string += ",";
        }
      }
      productionCountries.setText(string);
    }

    for (final Genre genre : currentMovieInfo.getGenres()) {
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
        if (getActivity() instanceof MovieActivity) {
          ((MovieActivity) getActivity()).onGenreClick(genre);
        }
      });
    }
  }

  private void dataLoadComplete() {
    if (++dataLoaded == DATA_TO_LOAD) {
      if (!checkInformation(currentMovieInfo) && currentMovieInfoEnglish == null) {
        dataLoaded--;
        currentMovieInfoEnglish = new MovieInfo(currentMovieInfo.getMediaId());
        loadInformationInto(currentMovieInfoEnglish, Locale.ENGLISH.getLanguage());
      } else {
        fillInformation();
        View progressBarPlaceholder = null;
        if (getView() != null) {
          progressBarPlaceholder = getView().findViewById(R.id.movie_info_progress_bar_placeholder);
        }
        if (progressBarPlaceholder != null) {
          progressBarPlaceholder.setVisibility(View.GONE);
        }
      }
    }
    Log.v(LOG_TAG, "data loaded:" + dataLoaded);
  }

}
