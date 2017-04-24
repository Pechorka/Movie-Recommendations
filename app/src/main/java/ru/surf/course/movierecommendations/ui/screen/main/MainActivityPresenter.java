package ru.surf.course.movierecommendations.ui.screen.main;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.agna.ferro.mvp.component.scope.PerScreen;
import java.util.List;
import javax.inject.Inject;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.RecommendedGenres;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.network.connection.NetworkConnectionChecker;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Filters;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Tasks;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.ui.screen.editPresets.EditPresetsView;
import ru.surf.course.movierecommendations.ui.screen.favorites.FavoritesActivityView;
import ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentPresenter;
import ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView;
import ru.surf.course.movierecommendations.ui.screen.settings.SettingsActivity;
import ru.surf.course.movierecommendations.util.Utilities;

@PerScreen
public class MainActivityPresenter extends BasePresenter<MainActivityView> {

    public static final String KEY_GENRE_IDS = "genre_ids";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_GENRE_NAME = "genre_name";

    private NetworkConnectionChecker networkConnectionChecker;
    private SharedPreferences sharedPreferences;
    private String region;
    private String query;

    private MediaListFragmentView movieListFragment;
    private MediaListFragmentView tvShowListFragment;
    private DBHelper dbHelper;

    @Inject
    public MainActivityPresenter(ErrorHandler errorHandler,
                                 NetworkConnectionChecker networkConnectionChecker,
                                 DBHelper dbHelper,
                                 SharedPreferences sharedPreferences) {
        super(errorHandler);
        this.networkConnectionChecker = networkConnectionChecker;
        this.dbHelper = dbHelper;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void onLoad(boolean viewRecreated) {
        super.onLoad(viewRecreated);

        if (networkConnectionChecker.hasInternetConnection()) {
            init();
            setup();
        } else {
            getView().showNoInternetMessage();
        }
    }

    @Override
    public void onResume() {
        Log.d("tag", "on resume");
        super.onResume();
        boolean newPreset = DBHelper.newPreset;
        if (newPreset) {
            setupPresetsSubMenu();
            DBHelper.newPreset = false;
        }
    }


    private void init() {
        region = Utilities.getSystemLanguage();

        query = sharedPreferences.getString("filter", Filters.popular.toString());
        Tasks task = Tasks.SEARCH_BY_FILTER;
        if (getView().getIntent().hasExtra(KEY_GENRE_IDS)) {
            query = getView().getIntent().getStringExtra(KEY_GENRE_IDS);
            task = Tasks.SEARCH_BY_GENRE;
        }
        if (query.equals(Filters.recommendations.toString())) {
            task = Tasks.SEARCH_RECOMMENDED_MEDIA;
        }
        if (query.equals(Filters.custom.toString())) {
            task = Tasks.SEARCH_BY_CUSTOM_FILTER;
        }
        switch (task) {
            case SEARCH_RECOMMENDED_MEDIA:
                initFragments(task, getRecommendedGenreIds(Media.MediaType.movie),
                        getRecommendedGenreIds(Media.MediaType.tv));
                break;
            default:
                initFragments(task);
        }
    }

    private void setup() {
        getView().initViewPager(movieListFragment, tvShowListFragment);


        if (getView().getIntent().hasExtra(KEY_GENRE_NAME)) {
            getView().setTitle(getView().getIntent().getStringExtra(KEY_GENRE_NAME));
        } else {
            setProperTitle();
        }
        Media.MediaType mediaType;
        if (getView().getIntent().hasExtra(KEY_MEDIA)) {
            mediaType = (Media.MediaType) getView().getIntent().getSerializableExtra(KEY_MEDIA);
        } else {
            mediaType = Media.MediaType.movie;
        }
        switch (mediaType) {
            case tv:
                getView().switchDrawer(mediaType);
                switch (mediaType) {
                    case movie:
                        if (query.equals(Filters.on_the_air.toString())) {
                            getView().setTitle(R.string.upcoming);
                        }
                        break;
                    case tv:
                        if (query.equals(Filters.on_the_air.toString())) {
                            getView().setTitle(R.string.on_air);
                        }
                        break;
                }
                break;
        }
        setupPresetsSubMenu();
    }

    private void setupPresetsSubMenu() {
        List<CustomFilter> customFilters = dbHelper.getAllCustomFilters();
        if (customFilters != null && customFilters.size() != 0) {
            getView().setPresetsSubMenu(customFilters);
        }
    }

    public boolean onPresetItemSelected(CustomFilter filter) {
        getView().setDrawerItemChecked(R.id.nav_custom, true);
        query = Filters.custom.toString();
        getView().setTitle(R.string.custom);
        getView().closeDrawer();
        byCustomFilter(filter);
        return true;
    }

    public boolean onEditPresetsSelected() {
        EditPresetsView.start(getView());
        return true;
    }



    public boolean onQueryTextSubmit(String query) {
        if (networkConnectionChecker.hasInternetConnection()) {
            searchByName(query.replace(' ', '+'));
        }
        return true;
    }

    public void onRetryClick() {
        if (networkConnectionChecker.hasInternetConnection()) {
            getView().hideNoInternetMessage();
            init();
            setup();
        }
    }

    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                if (query.equals(Filters.on_the_air.toString())) {
                    getView().setTitle(R.string.upcoming);
                }
                getView().switchDrawer(Media.MediaType.movie);
                break;
            case 1:
                if (query.equals(Filters.on_the_air.toString())) {
                    getView().setTitle(R.string.on_air);
                }
                getView().switchDrawer(Media.MediaType.tv);
                break;
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        String previousQuery = query;
        getView().closeDrawer();
        switch (menuItem.getItemId()) {
            case R.id.nav_recommended:
                query = Filters.recommendations.toString();
                getView().setTitle(R.string.recommend);
                loadInformationByGenreIds(getRecommendedGenreIds(Media.MediaType.movie),
                        getRecommendedGenreIds(Media.MediaType.tv));
                return;
            case R.id.nav_popular:
                query = Filters.popular.toString();
                getView().setTitle(R.string.popular);
                break;
            case R.id.nav_top:
                query = Filters.top_rated.toString();
                getView().setTitle(R.string.top);
                break;
            case R.id.nav_upcoming_or_on_air:
                query = Filters.upcoming.toString();
                getView().setTitle(R.string.upcoming);
                ((MediaListFragmentPresenter) movieListFragment.getPresenter())
                        .loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region, true);
                query = Filters.on_the_air.toString();
                ((MediaListFragmentPresenter) tvShowListFragment.getPresenter())
                        .loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region, true);
                return;
            case R.id.nav_custom:
                query = Filters.custom.toString();
                getView().setTitle(R.string.custom);
                break;
            case R.id.nav_favorite:
                getView().startActivity(FavoritesActivityView.class);
                return;
            case R.id.nav_settings:
                getView().startActivity(SettingsActivity.class);
                return;
        }
        if (previousQuery.equals(query)) {
            getView().showToast("Filter already selected");
            return;
        }

        loadInformationByFilter();
    }

    private void setProperTitle() {
        switch (query) {
            case "top_rated":
                getView().setTitle(R.string.top);
                getView().setDrawerItemChecked(R.id.nav_top, true);
                break;
            case "popular":
                getView().setTitle(R.string.popular);
                getView().setDrawerItemChecked(R.id.nav_popular, true);
                break;
            case "recommendations":
                getView().setTitle(R.string.recommend);
                getView().setDrawerItemChecked(R.id.nav_recommended, true);
                break;
            case "custom":
                getView().setTitle(R.string.custom);
                getView().setDrawerItemChecked(R.id.nav_custom, true);
                break;
        }
    }

    private String getRecommendedGenreIds(Media.MediaType mediaType) {
        List<? extends RecommendedGenres> ids = null;
        switch (mediaType) {
            case movie:
                ids = dbHelper.getAllRecommendedMovieGenres();
                break;
            case tv:
                ids = dbHelper.getAllRecommendedTVShowGenres();
                break;
        }
        StringBuilder builder = new StringBuilder();
        int i = 0;
        // Если добавить в запрос все жанры, он ничего не вернет(tmdb изменили работу этого запроса)
        while (i++ < 3) {
            int randomGenreIndex = getRandom(0, ids.size() - 1);
            builder.append(ids.get(randomGenreIndex).getGenreId()).append(",");
        }
        return builder.toString();
    }

    private int getRandom(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    private void initFragments(Tasks task) {
        movieListFragment = MediaListFragmentView.newInstance(query, region, task, Media.MediaType.movie);
        tvShowListFragment = MediaListFragmentView.newInstance(query, region, task, Media.MediaType.tv);
    }

    private void initFragments(Tasks task, String movieQuery, String tvshowQuery) {
        movieListFragment = MediaListFragmentView.newInstance(movieQuery, region, task, Media.MediaType.movie);
        tvShowListFragment = MediaListFragmentView.newInstance(tvshowQuery, region, task, Media.MediaType.tv);
    }

    private void byStandartFilter() {
        movieListFragment.setCallOptionsVisibility(View.GONE);
        ((MediaListFragmentPresenter) movieListFragment.getPresenter())
                .loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region, true);
        tvShowListFragment.setCallOptionsVisibility(View.GONE);
        ((MediaListFragmentPresenter) tvShowListFragment.getPresenter())
                .loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region, true);
    }

    private void byCustomFilter() {
        movieListFragment.setCallOptionsVisibility(View.VISIBLE);
        ((MediaListFragmentPresenter) movieListFragment.getPresenter())
                .loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), "1", region, true);
        tvShowListFragment.setCallOptionsVisibility(View.VISIBLE);
        ((MediaListFragmentPresenter) tvShowListFragment.getPresenter())
                .loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), "1", region, true);
    }

    private void byCustomFilter(CustomFilter customFilter) {
        movieListFragment.setCallOptionsVisibility(View.VISIBLE);
        ((MediaListFragmentPresenter) movieListFragment.getPresenter())
                .loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), "1", region, customFilter,
                        true);
        tvShowListFragment.setCallOptionsVisibility(View.VISIBLE);
        ((MediaListFragmentPresenter) tvShowListFragment.getPresenter())
                .loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), "1", region, customFilter,
                        true);
    }

    private void loadInformationBySearchQuery() {
        ((MediaListFragmentPresenter) movieListFragment.getPresenter()).loadMediaByName(query, Utilities.getSystemLanguage(), "1", true);
        ((MediaListFragmentPresenter) tvShowListFragment.getPresenter()).loadMediaByName(query, Utilities.getSystemLanguage(), "1", true);
    }

    private void searchByName(String name) {
        query = name;
        getView().setTitle("Search results");
        loadInformationBySearchQuery();
    }

    private void loadInformationByGenreIds(String movieGenreIds, String tvshowGenreIds) {
        ((MediaListFragmentPresenter) movieListFragment.getPresenter())
                .loadMediaInfoByGenreIds(movieGenreIds, Utilities.getSystemLanguage(), "1", region,
                        Tasks.SEARCH_BY_GENRE, true);
        ((MediaListFragmentPresenter) tvShowListFragment.getPresenter())
                .loadMediaInfoByGenreIds(tvshowGenreIds, Utilities.getSystemLanguage(), "1", region,
                        Tasks.SEARCH_BY_GENRE, true);
        query = movieGenreIds;
    }

    private void loadInformationByFilter() {
        if (!query.equals(Filters.custom.toString())) {
            byStandartFilter();
        } else {
            byCustomFilter();
        }
    }

    private boolean recommendedGenresAvailable() {
        return dbHelper.getAllRecommendedTVShowGenres() != null
                && dbHelper.getAllRecommendedTVShowGenres().size() != 0;
    }
}
