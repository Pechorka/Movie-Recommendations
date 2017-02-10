package ru.surf.course.movierecommendations;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import ru.surf.course.movierecommendations.adapters.ContentFragmentPagerAdapter;
import ru.surf.course.movierecommendations.fragments.MovieListFragment;
import ru.surf.course.movierecommendations.fragments.TVShowListFragment;
import ru.surf.course.movierecommendations.tmdbTasks.Filters;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

public class MainActivity extends AppCompatActivity {


    public static final String TAG_MOVIES_LIST_FRAGMENT = "movie_list_fragment";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    private MovieListFragment movieListFragment;
    private TVShowListFragment tvShowListFragment;

    public static void start(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private String language;
    private String query;
    private String previousQuery;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

//        if (!isInternetAvailable(this)) {
//            final TextView error = (TextView) findViewById(R.id.activity_main_text_internet_error);
//            error.setVisibility(View.VISIBLE);
//            final Button button = (Button) findViewById(R.id.activity_main_btn_reload);
//            button.setVisibility(View.VISIBLE);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isInternetAvailable(MainActivity.this)) {
//                        error.setVisibility(View.GONE);
//                        button.setVisibility(View.GONE);
//                    }
//                }
//            });
//            while (!isInternetAvailable(this)){
//
//            }
//        }
        setTitle(R.string.popular);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        language = Locale.getDefault().getLanguage();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        query = Filters.popular.toString();
        movieListFragment = MovieListFragment.newInstance(query,
                language, Tasks.SEARCH_BY_FILTER);
        tvShowListFragment = TVShowListFragment.newInstance(query,
                language, Tasks.SEARCH_BY_FILTER);
        viewPager.setAdapter(new ContentFragmentPagerAdapter(getSupportFragmentManager(),
                this, Filters.popular, movieListFragment, tvShowListFragment));

        nvDrawer.getMenu().add(R.id.nav_main, 2, 3, getResources().getString(R.string.upcoming));
        nvDrawer.getMenu().getItem(3).setIcon(R.drawable.upcoming_icon);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        nvDrawer.getMenu().removeItem(2);
                        nvDrawer.getMenu().add(R.id.nav_main, 2, 3, getResources().getString(R.string.upcoming));
                        nvDrawer.getMenu().getItem(3).setIcon(R.drawable.upcoming_icon);
                        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                selectDrawerItemMovies(item);
                                return true;
                            }
                        });
                        break;
                    case 1:
                        nvDrawer.getMenu().removeItem(2);
                        nvDrawer.getMenu().add(R.id.nav_main, 2, 3, getResources().getString(R.string.on_air));
                        nvDrawer.getMenu().getItem(3).setIcon(R.drawable.on_air_icon);
                        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                selectDrawerItemTVShows(item);
                                return true;
                            }
                        });
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItemMovies(item);
                return true;
            }
        });


    }

    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        mDrawer.setDrawerLockMode(lockMode);
        drawerToggle.setDrawerIndicatorEnabled(enabled);
    }

    public void selectDrawerItemMovies(MenuItem menuItem) {
        previousQuery = query;
        switch (menuItem.getItemId()) {
            case R.id.nav_popular:
                query = Filters.popular.toString();
                setTitle(R.string.popular);
                break;
            case R.id.nav_top:
                query = Filters.top_rated.toString();
                setTitle(R.string.top);
                break;
            case 2://upcoming
                query = Filters.upcoming.toString();
                setTitle(R.string.upcoming);
                break;
            case R.id.nav_custom:
                query = Filters.custom.toString();
                setTitle(R.string.custom);
                break;
        }
        if (previousQuery.equals(query)) {
            Toast.makeText(this, "Filter already selected", Toast.LENGTH_SHORT).show();
            return;
        }
        menuItem.setChecked(true);

        if (!query.equals(Filters.custom.toString())) {
            movieListFragment.setCallOptionsVisability(View.GONE);
            movieListFragment.loadMoviesInfoByFilter(query, language, "1");
        } else {
            movieListFragment.setCallOptionsVisability(View.VISIBLE);
            movieListFragment.loadMovieInfoByCustomFilter(language, "1");
        }
        mDrawer.closeDrawers();
    }

    public void selectDrawerItemTVShows(MenuItem menuItem) {
        previousQuery = query;
        switch (menuItem.getItemId()) {
            case R.id.nav_popular:
                query = Filters.popular.toString();
                setTitle(R.string.popular);
                break;
            case R.id.nav_top:
                query = Filters.top_rated.toString();
                setTitle(R.string.top);
                break;
            case 2:
                query = Filters.upcoming.toString();
                setTitle(R.string.upcoming);
                break;
            case R.id.nav_custom:
                query = Filters.custom.toString();
                setTitle(R.string.custom);
                break;
        }
        if (previousQuery.equals(query)) {
            Toast.makeText(this, "Filter already selected", Toast.LENGTH_SHORT).show();
            return;
        }
        menuItem.setChecked(true);

        if (!query.equals(Filters.custom.toString())) {
            tvShowListFragment.setCallOptionsVisability(View.GONE);
            tvShowListFragment.loadTVShowInfoByFilter(query, language, "1");
        } else {
            tvShowListFragment.setCallOptionsVisability(View.VISIBLE);
        }

        mDrawer.closeDrawers();
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
                if (isInternetAvailable(MainActivity.this)) {
//                    searchByName(query.replace(' ', '+'));
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


    //    private void loadMainFragment(Bundle savedInstanceState, String filter) {
//        if (savedInstanceState == null) {
//            final FragmentManager fragmentManager = getFragmentManager();
//            moviesListFragment = MoviesListFragment.newInstance(filter, Locale.getDefault().getLanguage(), Tasks.SEARCH_BY_FILTER);
//            fragmentManager.beginTransaction().add(R.id.activity_main_container, moviesListFragment, TAG_MOVIES_LIST_FRAGMENT).commit();
//        } else {
//            moviesListFragment = (MoviesListFragment) getFragmentManager().findFragmentByTag(TAG_MOVIES_LIST_FRAGMENT);
//        }
//    }

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(LOG_TAG, "no internet connection");
            return false;
        } else {
            if (info.isConnected()) {
                Log.d(LOG_TAG, " internet connection available...");
                return true;
            } else {
                return false;
            }

        }
    }


//    private void searchByName(String name) {
//        MoviesListFragment fragment = MoviesListFragment.newInstance(name, Locale.getDefault().getLanguage(), Tasks.SEARCH_BY_NAME, true);
//        switchContent(R.id.activity_main_container, fragment);
//    }

    public void switchContent(int id, Fragment fragment) {
        //noinspection ResourceType
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment).addToBackStack(null).commit();
    }

    public void switchContent(int id, Fragment fragment, int[] customAnimations) {
        //noinspection ResourceType
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(customAnimations[0], customAnimations[1], customAnimations[2], customAnimations[3])
                .replace(id, fragment).addToBackStack(null).commit();
    }
}
