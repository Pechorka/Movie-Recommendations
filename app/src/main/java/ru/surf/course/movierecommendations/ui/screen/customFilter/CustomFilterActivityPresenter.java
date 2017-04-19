package ru.surf.course.movierecommendations.ui.screen.customFilter;


import android.content.Intent;
import android.util.Log;

import com.agna.ferro.mvp.component.scope.PerScreen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.MediaType;
import ru.surf.course.movierecommendations.domain.genre.Genre;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.MovieGenre;
import ru.surf.course.movierecommendations.interactor.TVShowGenre;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetMovieGenresTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetTVShowGenresTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivity;
import ru.surf.course.movierecommendations.ui.screen.main.MediaListFragment;
import ru.surf.course.movierecommendations.util.Utilities;


@PerScreen
public class CustomFilterActivityPresenter extends BasePresenter<CustomFilterActivityView> {

    public static final String POPULARITY = "popularity";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    private final static String BASE_URL = "https://api.themoviedb.org";


    private DBHelper dbHelper;
    private List<? extends Genre> genres;
    private MediaType mediaType;
    private Retrofit retrofit;
    private Gson gson;


    @Inject
    public CustomFilterActivityPresenter(ErrorHandler errorHandler,
                                         DBHelper dbHelper) {
        super(errorHandler);
        this.dbHelper = dbHelper;
    }


    @Override
    public void onLoad(boolean viewRecreated) {
        super.onLoad(viewRecreated);

        if (getView().getIntent().hasExtra(MainActivity.KEY_MEDIA)) {
            mediaType = (MediaType) getView().getIntent().getSerializableExtra(MainActivity.KEY_MEDIA);
        }
        if (getView().getIntent().hasExtra(MediaListFragment.KEY_SORT_TYPE)) {
            getView().setupSortRG(getView().getIntent().getStringExtra(MediaListFragment.KEY_SORT_TYPE));
        }
        if (getView().getIntent().hasExtra(MediaListFragment.KEY_SORT_DIRECTION)) {
            getView().setupSortDirectionRG(getView().getIntent().getStringExtra(MediaListFragment.KEY_SORT_DIRECTION));
        }
        int maxYear = getView().getIntent()
                .getIntExtra(MediaListFragment.KEY_MAX_YEAR, Utilities.getCurrentYear());
        int minYear = getView().getIntent().getIntExtra(MediaListFragment.KEY_MIN_YEAR, 1930);
        getView().setYearsRangeBarMinValue(minYear);
        getView().setYearsRangeBarMaxValue(maxYear);

        init();
    }

    public void init() {
        genres = new ArrayList<>();
        gson = new GsonBuilder()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        loadGenres();
    }

    public boolean onOptionsItemSelected(int id) {
        switch (id) {
            case android.R.id.home:
                getView().onBackPressed();
                return true;
            case R.id.custom_filter_menu_save:
                Intent intent = new Intent();
                intent.putExtra(MediaListFragment.KEY_MIN_YEAR, getView().getYearsRangeBarMinYear());
                intent.putExtra(MediaListFragment.KEY_MAX_YEAR, getView().getYearsRangeBarMaxYear());
                intent.putExtra(MediaListFragment.KEY_GENRES, getView().getCheckedGenres());
                intent.putExtra(MediaListFragment.KEY_SORT_TYPE, getView().getSortType());
                intent.putExtra(MediaListFragment.KEY_SORT_DIRECTION, getView().getSortDirection());
                getView().returnWithResults(intent);
                return true;
            case R.id.custom_filter_menu_save_preset:
                if (dbHelper.canAddCustomFilter()) {
                    getView().showCustomFilterDialog("save_filter_dialog", mediaType);
                } else {
                    getView().showMessage(R.string.to_many_presets);
                }
                return true;
            default:
                return false;
        }
    }

    private void loadGenres() {
        loadGenresFromDB();
        if (genres == null || genres.size() == 0) {
            switch (mediaType) {
                case movie:
                    loadMovieGenresFromTMDB();
                    break;
                case tv:
                    loadTVShowGenresFromTMDB();
                    break;
            }
        }
    }

    private void loadGenresFromDB() {
        switch (mediaType) {
            case movie:
                genres = dbHelper.getAllMovieGenres();
                break;
            case tv:
                genres = dbHelper.getAllTVShowGenres();
                break;
        }
        getView().setGenresListContent(genres);
    }

    private void loadMovieGenresFromTMDB() {
        Callback<MovieGenre.RetrofitResult> callback = new Callback<MovieGenre.RetrofitResult>() {
            @Override
            public void onResponse(Call<MovieGenre.RetrofitResult> call,
                                   Response<MovieGenre.RetrofitResult> response) {
                if (response.isSuccessful()) {
                    genres = response.body().movieGenres;
                    getView().setGenresListContent(genres);
                    dbHelper.addMovieGenres(response.body().movieGenres);
                }
            }

            @Override
            public void onFailure(Call<MovieGenre.RetrofitResult> call, Throwable t) {
                Log.d("tag", t.getMessage());
            }
        };
        GetMovieGenresTask task = retrofit.create(GetMovieGenresTask.class);
        Call<MovieGenre.RetrofitResult> call = task
                .getGenres(mediaType.toString(), BuildConfig.TMDB_API_KEY);
        call.enqueue(callback);
    }

    private void loadTVShowGenresFromTMDB() {
        Callback<TVShowGenre.RetrofitResult> callback = new Callback<TVShowGenre.RetrofitResult>() {
            @Override
            public void onResponse(Call<TVShowGenre.RetrofitResult> call,
                                   Response<TVShowGenre.RetrofitResult> response) {
                if (response.isSuccessful()) {
                    genres = response.body().tvShowGenres;
                    getView().setGenresListContent(genres);
                    dbHelper.addTVShowGenres(response.body().tvShowGenres);
                }
            }

            @Override
            public void onFailure(Call<TVShowGenre.RetrofitResult> call, Throwable t) {

            }
        };
        GetTVShowGenresTask task = retrofit.create(GetTVShowGenresTask.class);
        Call<TVShowGenre.RetrofitResult> call = task
                .getGenres(mediaType.toString(), BuildConfig.TMDB_API_KEY);
        call.enqueue(callback);
    }
}
