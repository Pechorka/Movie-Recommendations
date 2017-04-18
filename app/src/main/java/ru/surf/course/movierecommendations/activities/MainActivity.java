package ru.surf.course.movierecommendations.activities;


import static ru.surf.course.movierecommendations.models.MediaType.tv;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.List;
import ru.surf.course.movierecommendations.DBHelper;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.adapters.ContentFragmentPagerAdapter;
import ru.surf.course.movierecommendations.fragments.MediaListFragment;
import ru.surf.course.movierecommendations.models.MediaType;
import ru.surf.course.movierecommendations.models.RecommendedGenres;
import ru.surf.course.movierecommendations.tmdbTasks.Filters;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

public class MainActivity extends AppCompatActivity {

  public static final String KEY_SEARCH_QUERY = "search_query";
  public static final String KEY_GENRE_IDS = "genre_ids";
  public static final String KEY_MEDIA = "media";
  public static final String KEY_GENRE_NAME = "genre_name";
  private static final String LOG_TAG = MainActivity.class.getSimpleName();
  private static final int UPCOMING_ID = 2;
  private static final int ON_AIR_ID = 3;
  private static final int UPCOMING_POSITION = 3;
  private static final int ON_AIR_POSITION = 3;

  private DrawerLayout mDrawer;
  private NavigationView nvDrawer;
  private ActionBarDrawerToggle drawerToggle;
  private ViewPager viewPager;
  private TabLayout tabLayout;

  private String region;
  private String query;
  private String previousQuery;

  private MediaListFragment movieListFragment;
  private MediaListFragment tvShowListFragment;
  private DBHelper dbHelper;

  public static void start(Context context, Class c) {
    Intent intent = new Intent(context, c);
    context.startActivity(intent);
  }

  public static void start(Context context, Class c, String ids, String genreName, boolean movie) {
    Intent intent = new Intent(context, c);
    intent.putExtra(KEY_GENRE_IDS, ids);
    intent.putExtra(KEY_MEDIA, movie);
    intent.putExtra(KEY_GENRE_NAME, genreName);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (Utilities.isConnectionAvailable(this)) {
      init();
      setup();
    } else {
      final View errorPlaceholder = findViewById(R.id.main_no_internet_screen);
      errorPlaceholder.setVisibility(View.VISIBLE);
      (findViewById(R.id.message_no_internet_button))
          .setOnClickListener(view -> {
            if (Utilities.isConnectionAvailable(MainActivity.this)) {
              errorPlaceholder.setVisibility(View.GONE);
              init();
              setup();
            }
          });
    }

  }

  @Override
  public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onPostCreate(savedInstanceState, persistentState);
    drawerToggle.syncState();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setInputType(InputType.TYPE_CLASS_TEXT);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (Utilities.isConnectionAvailable(MainActivity.this)) {
          searchByName(query.replace(' ', '+'));
        }
        searchView.clearFocus();
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    switch (item.getItemId()) {
      case android.R.id.home:
        mDrawer.openDrawer(GravityCompat.START);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    dbHelper.clearMoiveGenres();
    dbHelper.clearTVShowGenres();
  }

  private void init() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
    setSupportActionBar(toolbar);
    mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    nvDrawer = (NavigationView) findViewById(R.id.nvView);
    region = Utilities.getSystemLanguage();
    drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,
        R.string.drawer_close);
    viewPager = (ViewPager) findViewById(R.id.viewpager);
    dbHelper = DBHelper.getHelper(this);
    Tasks task = Tasks.SEARCH_BY_FILTER;
    query = Filters.popular.toString();
    if (getIntent().hasExtra(KEY_GENRE_IDS)) {
      query = getIntent().getStringExtra(KEY_GENRE_IDS);
      task = Tasks.SEARCH_BY_GENRE;
    } else {
      if (recommendedGenresAvailable()) {
        task = Tasks.SEARCH_RECOMMENDED_MEDIA;
      }
    }
    switch (task) {
      case SEARCH_RECOMMENDED_MEDIA:
        initFragments(task, getRecommendedGenreIds(MediaType.movie),
            getRecommendedGenreIds(tv));
        break;
      default:
        initFragments(task);
    }

  }

  private void initFragments(Tasks task) {
    movieListFragment = MediaListFragment.newInstance(query, region, task, MediaType.movie);
    tvShowListFragment = MediaListFragment.newInstance(query, region, task, tv);
  }

  private void initFragments(Tasks task, String movieQuery, String tvshowQuery) {
    movieListFragment = MediaListFragment.newInstance(movieQuery, region, task, MediaType.movie);
    tvShowListFragment = MediaListFragment.newInstance(tvshowQuery, region, task, tv);
  }

  private void setup() {
    mDrawer.addDrawerListener(drawerToggle);
    //?????
    drawerToggle.setDrawerIndicatorEnabled(false);
    drawerToggle.setDrawerIndicatorEnabled(true);

    viewPager.setAdapter(new ContentFragmentPagerAdapter(getSupportFragmentManager(),
        this, query, movieListFragment, tvShowListFragment));

    tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
    tabLayout.setupWithViewPager(viewPager);
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        switch (position) {
          case 0:
            switchDrawer(MediaType.movie);
            break;
          case 1:
            switchDrawer(tv);
            break;
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    if (getIntent().hasExtra(KEY_GENRE_NAME)) {
      setTitle(getIntent().getStringExtra(KEY_GENRE_NAME));
    } else {
      if (query.equals(Filters.popular.toString())) {
        setTitle(R.string.popular);
        nvDrawer.getMenu().getItem(1).setChecked(true);
      } else {
        nvDrawer.getMenu().getItem(0).setChecked(true);
        setTitle(R.string.recommend);
      }
    }
    MediaType mediaType;
    if (getIntent().hasExtra(KEY_MEDIA)) {
      mediaType = (MediaType) getIntent().getSerializableExtra(KEY_MEDIA);
    } else {
      mediaType = MediaType.movie;
    }
    switch (mediaType) {
      case tv:
        switchDrawer(mediaType);
        break;
    }
    nvDrawer
        .setNavigationItemSelectedListener(item -> {
          selectDrawerItem(item);
          return true;
        });

  }

  public void setDrawerEnabled(boolean enabled) {
    if (mDrawer != null) {
      int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
          DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
      mDrawer.setDrawerLockMode(lockMode);
      drawerToggle.setDrawerIndicatorEnabled(enabled);
    }
  }

  private void switchDrawer(final MediaType mediaType) {
    MenuItem menuItem = nvDrawer.getMenu().findItem(R.id.nav_upcoming_or_on_air);
    switch (mediaType) {
      case movie:
        menuItem.setIcon(R.drawable.upcoming_icon);
        menuItem.setTitle(R.string.upcoming);
        if (query.equals(Filters.on_the_air.toString())) {
          setTitle(R.string.upcoming);
        }
        break;
      case tv:
        menuItem.setIcon(R.drawable.on_air_icon);
        menuItem.setTitle(R.string.on_air);
        if (query.equals(Filters.on_the_air.toString())) {
          setTitle(R.string.on_air);
        }
        break;
    }

    nvDrawer
        .setNavigationItemSelectedListener(item -> {
          selectDrawerItem(item);
          return true;
        });

  }

  private void selectDrawerItem(MenuItem menuItem) {
    previousQuery = query;
    mDrawer.closeDrawers();
    switch (menuItem.getItemId()) {
      case R.id.nav_recommended:
        query = Filters.recommendations.toString();
        setTitle(R.string.recommend);
        loadInformationByGenreIds(getRecommendedGenreIds(MediaType.movie),
            getRecommendedGenreIds(MediaType.tv));
        return;
      case R.id.nav_popular:
        query = Filters.popular.toString();
        setTitle(R.string.popular);
        break;
      case R.id.nav_top:
        query = Filters.top_rated.toString();
        setTitle(R.string.top);
        break;
      case R.id.nav_upcoming_or_on_air:
        query = Filters.upcoming.toString();
        setTitle(R.string.upcoming);
        movieListFragment.loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region);
        query = Filters.on_the_air.toString();
        tvShowListFragment.loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region);
        return;
      case R.id.nav_custom:
        query = Filters.custom.toString();
        setTitle(R.string.custom);
        break;
      case R.id.nav_favorite:
        start(this, FavoritesActivity.class);
        return;
      case R.id.nav_settings:
        start(this, SettingsActivity.class);
        return;
    }
    if (previousQuery.equals(query)) {
      Toast.makeText(this, "Filter already selected", Toast.LENGTH_SHORT).show();
      return;
    }
    menuItem.setChecked(true);

    loadInformationByFilter();
  }

  private void loadInformationByFilter() {
    if (!query.equals(Filters.custom.toString())) {
      movieListFragment.setCallOptionsVisibility(View.GONE);
      movieListFragment
          .loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region);
      tvShowListFragment.setCallOptionsVisibility(View.GONE);
      tvShowListFragment
          .loadMediaInfoByFilter(query, Utilities.getSystemLanguage(), "1", region);
    } else {
      movieListFragment.setCallOptionsVisibility(View.VISIBLE);
      movieListFragment.loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), "1", region);
      tvShowListFragment.setCallOptionsVisibility(View.VISIBLE);
      tvShowListFragment
          .loadMediaInfoByCustomFilter(Utilities.getSystemLanguage(), "1", region);
    }
  }

  private void loadInformationBySearchQuery() {
    movieListFragment.loadMediaByName(query, Utilities.getSystemLanguage(), "1");
    tvShowListFragment.loadMediaByName(query, Utilities.getSystemLanguage(), "1");
  }

  private void searchByName(String name) {
    query = name;
    setTitle("Search results");
    loadInformationBySearchQuery();
  }

  private void loadInformationByGenreIds(String movieGenreIds, String tvshowGenreIds) {
    movieListFragment.loadMediaInfoByIds(movieGenreIds, Utilities.getSystemLanguage(), "1", region,
        Tasks.SEARCH_BY_GENRE);
    tvShowListFragment.loadMediaInfoByIds(movieGenreIds, Utilities.getSystemLanguage(), "1", region,
        Tasks.SEARCH_BY_GENRE);
    query = movieGenreIds;
  }

  private MediaType getCurrentTab() {
    switch (tabLayout.getSelectedTabPosition()) {
      case 1:
        return tv;
      case 0:
      default:
        return MediaType.movie;

    }
  }

  private String getRecommendedGenreIds(MediaType mediaType) {
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
    if (ids != null) {
      for (int i = 0; i < ids.size(); i++) {
        builder.append(ids.get(i).getGenre_id()).append(", ");
      }
    }
    return builder.toString();
  }

  private boolean recommendedGenresAvailable() {
    return dbHelper.getAllRecommendedTVShowGenres() != null
        && dbHelper.getAllRecommendedTVShowGenres().size() != 0;
  }

  public void switchContent(int id, Fragment fragment) {
    //noinspection ResourceType
    getSupportFragmentManager().beginTransaction()
        .replace(id, fragment).addToBackStack(null).commit();
  }

  public void switchContent(int id, Fragment fragment, int[] customAnimations) {
    //noinspection ResourceType
    getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(customAnimations[0], customAnimations[1], customAnimations[2],
            customAnimations[3])
        .replace(id, fragment).addToBackStack(null).commit();
  }
}
