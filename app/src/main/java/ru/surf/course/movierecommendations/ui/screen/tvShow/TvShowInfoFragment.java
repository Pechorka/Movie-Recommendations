package ru.surf.course.movierecommendations.ui.screen.tvShow;

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
import ru.surf.course.movierecommendations.domain.Media.MediaType;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowTask;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.adapters.CreditsOfPeopleListAdapter;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.adapters.ImagesListAdapter;
import ru.surf.course.movierecommendations.ui.screen.person.PersonActivity;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by andrew on 2/19/17.
 */

public class TvShowInfoFragment extends Fragment {

  final static String KEY_TV_SHOW = "tv";
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
  private ImagesListAdapter mImagesListAdapter;
  private RecyclerView credits;
  private CreditsOfPeopleListAdapter mCreditsOfPeopleListAdapter;
  private FlowLayout genres;
  private TextView episodeRuntime;
  private TextView status;
  private TextView numberOfSeasons;

  private Retrofit retrofit;
  private String apiKey;

  private int dataLoaded = 0;

  public static TvShowInfoFragment newInstance(
      TVShowInfo tvShow) {  //considering this object already has all info
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
    progressBar = (ProgressBar) root.findViewById(R.id.tv_show_info_progress_bar);
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

    RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory
        .createWithScheduler(Schedulers.io());
    Gson gson = new GsonBuilder().create();
    retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(rxAdapter)
        .build();

    apiKey = BuildConfig.TMDB_API_KEY;
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
      loadInformationInto(currentTvShowInfo, Utilities.getSystemLanguage());
    }
  }

  private void loadInformationInto(final TVShowInfo tvShow, String language) {

    GetTVShowTask getTVShowTask = retrofit.create(GetTVShowTask.class);
    Observable<TVShowInfo> call = getTVShowTask
        .getTVShowById(tvShow.getMediaId(), apiKey, language);
    call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShowInfo -> {
          tvShow.fillFields(tvShowInfo);
          dataLoadComplete();
          loadBackdropsInto(currentTvShowInfo);
          loadCreditsInto(currentTvShowInfo);
        });
  }

  private void loadBackdropsInto(final TVShowInfo tvShow) {
    GetImagesTask getImagesTask = retrofit.create(GetImagesTask.class);
    Observable<TmdbImage.RetrofitResultPosters> call = getImagesTask
        .getPostersBackdrops(MediaType.movie.toString(), tvShow.getMediaId(), apiKey);
    call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(tmdbImages -> {
          tvShow.setBackdrops(tmdbImages.backdrops);
          dataLoadComplete();
        });
  }

  private void loadCreditsInto(final TVShowInfo tvShowInfo) {
    GetCreditsTask getCreditsTask = new GetCreditsTask();
    getCreditsTask.addListener(result -> {
      tvShowInfo.setCredits(result);
      dataLoadComplete();
    });
    getCreditsTask.getTVShowCredits(tvShowInfo.getMediaId());
  }

  private boolean checkInformation(TVShowInfo tvShow) {
    return Utilities.checkString(tvShow.getOverview());
    //for now checking only overview
  }

  private String firstLetterToUpper(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }


  private void fillInformation() {

    if (Utilities.checkString(currentTvShowInfo.getOverview())) {
      overview.setText(currentTvShowInfo.getOverview());
    } else {
      overview.setText(currentTvShowInfoEnglish.getOverview());
    }

    overview.post(() -> {
      if (overview.getLineCount() >= overview.getMaxLines()) {
        expandCollapseOverviewButton.setVisibility(View.VISIBLE);
      }
    });

    voteAverage.setText(String.valueOf(currentTvShowInfo.getVoteAverage()));

    status.setText(currentTvShowInfo.getStatus());

    episodeRuntime.setText(String.valueOf(currentTvShowInfo.getEpisodesRuntime().get(0)));

    numberOfSeasons.setText(String.valueOf(currentTvShowInfo.getNumberOfSeasons()));

    mImagesListAdapter = new ImagesListAdapter(currentTvShowInfo.getBackdrops(), getActivity());
    mImagesListAdapter.setOnItemClickListener(position -> {
      ArrayList<String> paths = new ArrayList<String>();
      for (TmdbImage image :
          mImagesListAdapter.getImages()) {
        paths.add(image.path);
      }
      GalleryActivityView.start(getActivity(), paths, position);
    });
    backdrops.setAdapter(mImagesListAdapter);

    mCreditsOfPeopleListAdapter = new CreditsOfPeopleListAdapter(currentTvShowInfo.getCredits(),
        getActivity());
    mCreditsOfPeopleListAdapter
        .setOnItemClickListener(position -> PersonActivity.start(getActivity(),
            mCreditsOfPeopleListAdapter.getCredits().get(position).getPerson()));
    credits.setAdapter(mCreditsOfPeopleListAdapter);

    for (final Genre genre : currentTvShowInfo.getGenres()) {
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
        if (getActivity() instanceof TvShowActivity) {
          ((TvShowActivity) getActivity()).onGenreClick(genre);
        }
      });
    }
  }

  private void dataLoadComplete() {
    if (++dataLoaded == DATA_TO_LOAD) {
      if (!checkInformation(currentTvShowInfo) && currentTvShowInfoEnglish == null) {
        dataLoaded--;
        currentTvShowInfoEnglish = new TVShowInfo(currentTvShowInfo.getMediaId());
        loadInformationInto(currentTvShowInfoEnglish, Locale.ENGLISH.getLanguage());
      } else {
        fillInformation();
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
    Log.v(LOG_TAG, "data loaded:" + dataLoaded);
  }

}
