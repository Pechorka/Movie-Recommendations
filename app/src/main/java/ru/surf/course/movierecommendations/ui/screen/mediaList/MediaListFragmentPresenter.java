package ru.surf.course.movierecommendations.ui.screen.mediaList;


import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.DESC;
import static ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityPresenter.POPULARITY;
import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_MEDIA;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_GENRES;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_MAX_YEAR;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_MEDIA_ID;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_MIN_YEAR;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_QUERY;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_REGION;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_SORT_DIRECTION;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_SORT_TYPE;
import static ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView.KEY_TASK;

import android.content.Intent;
import com.agna.ferro.mvp.component.scope.PerScreen;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMovieTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Tasks;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;

@PerScreen
public class MediaListFragmentPresenter extends BasePresenter<MediaListFragmentView> {

    private List<Media> mediaList;
    private Tasks task;
    private int page;
    private String query;
    private String region;
    private String ids;
    private int id;
    private Media.MediaType mediaType;
    private String maxYear;
    private String minYear;
    private String genreIds;
    private String sort_type;
    private String sort_direction;
    private String apiKey;

    private boolean newResult;

    private Retrofit retrofit;
    private GetMovieTask getMovieTask;
    private GetTVShowTask getTVShowTask;

    @Inject
    public MediaListFragmentPresenter(ErrorHandler errorHandler,
                                      Retrofit retrofit) {
        super(errorHandler);
        this.retrofit = retrofit;
    }

    @Override
    public void onLoad(boolean viewRecreated) {
        super.onLoad(viewRecreated);

        init();
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        loadInformation(task);
    }

    private void init() {
        query = getView().getArguments().getString(KEY_QUERY);
        region = getView().getArguments().getString(KEY_REGION);
        task = (Tasks) getView().getArguments().getSerializable(KEY_TASK);
        id = getView().getArguments().getInt(KEY_MEDIA_ID);
        ids = query;
        mediaType = (Media.MediaType) getView().getArguments().getSerializable(KEY_MEDIA);
        page = 1;
        apiKey = BuildConfig.TMDB_API_KEY;
        maxYear = String.valueOf(new GregorianCalendar().get(Calendar.YEAR));
        minYear = "1930";
        genreIds = "";
        sort_type = POPULARITY;
        sort_direction = DESC;
        mediaList = new ArrayList<>();
        newResult = true;
        getMovieTask = retrofit.create(GetMovieTask.class);
        getTVShowTask = retrofit.create(GetTVShowTask.class);
    }

    private void loadInformation(Tasks task) {
        switch (task) {
            case SEARCH_BY_FILTER:
                loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), String.valueOf(page), region,
                        newResult);
                break;
            case SEARCH_BY_CUSTOM_FILTER:
                loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), String.valueOf(page), region,
                        newResult);
                break;
            case SEARCH_BY_GENRE:
            case SEARCH_RECOMMENDED_MEDIA:
                loadMediaInfoByGenreIds(ids, Utilities.getSystemLanguage(), String.valueOf(page), region,
                        task,
                        newResult);
                break;
            case SEARCH_BY_KEYWORD:
                loadMediaInfoByKeywords(ids, Utilities.getSystemLanguage(), String.valueOf(page), region,
                        newResult);
                break;
            case SEARCH_SIMILAR:
                loadMediaInfoById(id, Utilities.getSystemLanguage(), String.valueOf(page), task, newResult);
                break;
            case SEARCH_BY_NAME:
                loadMediaByName(query, Utilities.getSystemLanguage(), String.valueOf(page), newResult);
            default:
                loadMediaByName(query, Utilities.getSystemLanguage(), String.valueOf(page), newResult);
        }
    }

    public void loadMediaByName(String query, String language, String page, boolean newResult) {
        //TODO fix
        switch (mediaType) {
            case movie:
                Observable<MovieInfo.RetrofitResult> movieCall = getMovieTask
                        .getMediaByName(mediaType.toString(), query, apiKey, language, page);
                subscribeNetworkQuery(movieCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
            case tv:
                Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTask
                        .getMediaByName(mediaType.toString(), query, apiKey, language, page);
                subscribeNetworkQuery(tvshowCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
        }
        task = Tasks.SEARCH_BY_NAME;
        this.newResult = newResult;
    }

    public void loadMediaInfoByFilter(String query, String language, String page, String region,
                                      boolean newResult) {
        switch (mediaType) {
            case movie:
                Observable<MovieInfo.RetrofitResult> movieCall = getMovieTask
                        .getMediaByFilter(mediaType.toString(), query, apiKey, language, page, region);
                subscribeNetworkQuery(movieCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
            case tv:
                Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTask
                        .getMediaByFilter(mediaType.toString(), query, apiKey, language, page, region);
                subscribeNetworkQuery(tvshowCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
        }
        this.page = Integer.parseInt(page);
        task = Tasks.SEARCH_BY_FILTER;
        this.newResult = newResult;
    }

    public void loadMediaInfoByGenreIds(String ids, String language, String page, String region,
                                        Tasks task, boolean newResult) {
        if (task == Tasks.SEARCH_BY_GENRE || task == Tasks.SEARCH_BY_KEYWORD) {
            switch (mediaType) {
                case movie:
                    Observable<MovieInfo.RetrofitResult> movieCall = getMovieTask
                            .getMediaByGenreIds(mediaType.toString(), apiKey, language, page, region, ids);
                    subscribeNetworkQuery(movieCall,
                            (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                    break;
                case tv:
                    Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTask
                            .getMediaByGenreIds(mediaType.toString(), apiKey, language, page, region, ids);
                    subscribeNetworkQuery(tvshowCall,
                            (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                    break;
            }
            this.page = Integer.parseInt(page);
            this.task = task;
            this.newResult = newResult;
        }
    }


    private void loadMediaInfoByKeywords(String keywords, String language, String page, String region,
                                         boolean newResult) {
        switch (mediaType) {
            case movie:
                Observable<MovieInfo.RetrofitResult> movieCall = getMovieTask
                        .getMediaByKeywords(mediaType.toString(), apiKey, language, page, region, keywords);
                subscribeNetworkQuery(movieCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
            case tv:
                Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTask
                        .getMediaByKeywords(mediaType.toString(), apiKey, language, page, region, keywords);
                subscribeNetworkQuery(tvshowCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
        }
        this.page = Integer.parseInt(page);
        this.newResult = newResult;

    }

    private void loadMediaInfoById(int id, String language, String page, Tasks task,
                                   boolean newResult) {
        switch (mediaType) {
            case movie:
                Observable<MovieInfo.RetrofitResult> movieCall = getMovieTask
                        .getSimilarMedia(mediaType.toString(), id, apiKey, language, page);
                subscribeNetworkQuery(movieCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
            case tv:
                Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTask
                        .getSimilarMedia(mediaType.toString(), id, apiKey, language, page);
                subscribeNetworkQuery(tvshowCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
        }
        this.page = Integer.parseInt(page);
        this.task = task;
        this.newResult = newResult;
    }

    public void loadMediaInfoByCustomFilter(String language, String page, String region,
                                            boolean newResult) {
        String sortBy = sort_type + "." + sort_direction;
        switch (mediaType) {
            case movie:
                Observable<MovieInfo.RetrofitResult> movieCall = getMovieTask
                        .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                                minYear, maxYear, sortBy);
                subscribeNetworkQuery(movieCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
            case tv:
                Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTask
                        .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                                minYear, maxYear, sortBy);
                subscribeNetworkQuery(tvshowCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
        }
        this.page = Integer.parseInt(page);
        task = Tasks.SEARCH_BY_CUSTOM_FILTER;
        this.newResult = newResult;
    }


    public void loadMediaInfoByCustomFilter(String language, String page, String region,
                                            CustomFilter customFilter, boolean newResult) {
        if (mediaType == customFilter.getMediaType()) {
            applyCustomFilter(customFilter);
        }
        String sortBy = sort_type + "." + sort_direction;
        switch (mediaType) {
            case movie:
                Observable<MovieInfo.RetrofitResult> movieCall = getMovieTask
                        .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                                minYear, maxYear, sortBy);
                subscribeNetworkQuery(movieCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
            case tv:
                Observable<TVShowInfo.RetrofitResult> tvshowCall = getTVShowTask
                        .getMediaByCustomFilter(mediaType.toString(), apiKey, language, page, region, genreIds,
                                minYear, maxYear, sortBy);
                subscribeNetworkQuery(tvshowCall,
                        (retrofitResult) -> dataLoadComplete(retrofitResult.results));
                break;
        }
        this.page = Integer.parseInt(page);
        task = Tasks.SEARCH_BY_CUSTOM_FILTER;
        this.newResult = newResult;
    }

    public void onGetGenresResult(Intent data) {
        maxYear = String
                .valueOf(data.getIntExtra(KEY_MAX_YEAR, new GregorianCalendar().get(Calendar.YEAR)));
        minYear = String.valueOf(data.getIntExtra(KEY_MIN_YEAR, 1930));
        genreIds = data.getStringExtra(KEY_GENRES);
        sort_type = data.getStringExtra(KEY_SORT_TYPE);
        sort_direction = data.getStringExtra(KEY_SORT_DIRECTION);
        page = 1;
        newResult = true;
        loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), String.valueOf(page), region,
                newResult);
    }

    public void onLoadMore() {
        this.page++;
        loadInformation(task);
    }

    public void onCustomFilterBtnClick() {
      getView().startCustomFilterActivity(sort_type, sort_direction, mediaType, maxYear, minYear,
          genreIds);
    }

    private void applyCustomFilter(CustomFilter customFilter) {
        sort_type = customFilter.getSortType();
        sort_direction = customFilter.getSortDirection();
        genreIds = customFilter.getGenreIds();
        maxYear = customFilter.getMaxYear();
        minYear = customFilter.getMinYear();
    }

    private void dataLoadComplete(List<? extends Media> result) {
        if (newResult) {
            newResult = false;
            mediaList.clear();
        }
        mediaList.addAll(result);
        getView().fillInformation(mediaList);
        getView().hidePlaceholder();
    }
}
