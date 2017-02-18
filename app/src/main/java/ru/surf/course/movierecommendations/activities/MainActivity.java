package ru.surf.course.movierecommendations.activities;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.adapters.ContentFragmentPagerAdapter;
import ru.surf.course.movierecommendations.fragments.MediaListFragment;
import ru.surf.course.movierecommendations.tmdbTasks.Filters;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_SEARCH_QUERY = "search_query";
    public static final String KEY_GENRE_IDS = "genre_ids";
    public static final String KEY_MOVIE = "movie";
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

    private String language;
    private String query;
    private String previousQuery;
    private int movieLastDrawerItemId;
    private int tvshowLastDrawerItemId;
    private MediaListFragment movieListFragment;
    private MediaListFragment tvShowListFragment;

    public static void start(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static void start(Context context, Class c, String ids, String genreName, boolean movie) {
        Intent intent = new Intent(context, c);
        intent.putExtra(KEY_GENRE_IDS, ids);
        intent.putExtra(KEY_MOVIE, movie);
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
            (findViewById(R.id.message_no_internet_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utilities.isConnectionAvailable(MainActivity.this)) {
                        errorPlaceholder.setVisibility(View.GONE);
                        init();
                        setup();
                    }
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
                    //0 - movie tab
                    searchByName(query.replace(' ', '+'), tabLayout.getSelectedTabPosition() == 0);
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

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        if (getIntent().hasExtra(KEY_GENRE_NAME)) {
            setTitle(getIntent().getStringExtra(KEY_GENRE_NAME));
        } else {
            setTitle(R.string.popular);
        }
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        language = Locale.getDefault().getLanguage();
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (getIntent().hasExtra(KEY_GENRE_IDS)) {
            query = getIntent().getStringExtra(KEY_GENRE_IDS);
            movieListFragment = MediaListFragment.newInstance(query,
                    language, Tasks.SEARCH_BY_GENRE, true);
            tvShowListFragment = MediaListFragment.newInstance(query,
                    language, Tasks.SEARCH_BY_GENRE, false);
        } else {
            query = Filters.popular.toString();
            movieListFragment = MediaListFragment.newInstance(query,
                    language, Tasks.SEARCH_BY_FILTER, true);
            tvShowListFragment = MediaListFragment.newInstance(query,
                    language, Tasks.SEARCH_BY_FILTER, false);
        }

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
        nvDrawer.getMenu().add(R.id.nav_main, UPCOMING_ID, UPCOMING_POSITION, getResources().getString(R.string.upcoming));
        nvDrawer.getMenu().getItem(UPCOMING_POSITION).setIcon(R.drawable.upcoming_icon);
        nvDrawer.getMenu().getItem(0).setChecked(true);
        movieLastDrawerItemId = tvshowLastDrawerItemId = R.id.nav_popular;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        movieDrawer();
                        break;
                    case 1:
                        tvShowDrawer();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (!getIntent().getBooleanExtra(KEY_MOVIE, true)) {
            tvShowDrawer();
            viewPager.setCurrentItem(1);
            nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectDrawerItem(item, false);
                    return true;
                }
            });
        } else {
            nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectDrawerItem(item, true);
                    return true;
                }
            });
        }

    }

    public void setDrawerEnabled(boolean enabled) {
        if (mDrawer != null) {
            int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
            mDrawer.setDrawerLockMode(lockMode);
            drawerToggle.setDrawerIndicatorEnabled(enabled);
        }
    }

    private void movieDrawer() {
        nvDrawer.getMenu().removeItem(ON_AIR_POSITION);
        nvDrawer.getMenu().add(R.id.nav_main, UPCOMING_ID, UPCOMING_POSITION, getResources().getString(R.string.upcoming));
        nvDrawer.getMenu().getItem(UPCOMING_POSITION).setIcon(R.drawable.upcoming_icon);
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item, true);
                return true;
            }
        });
        nvDrawer.getMenu().findItem(movieLastDrawerItemId).setChecked(true);
    }

    private void tvShowDrawer() {
        nvDrawer.getMenu().removeItem(UPCOMING_ID);
        nvDrawer.getMenu().add(R.id.nav_main, ON_AIR_ID, ON_AIR_POSITION, getResources().getString(R.string.on_air));
        nvDrawer.getMenu().getItem(ON_AIR_POSITION).setIcon(R.drawable.on_air_icon);
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item, false);
                return true;
            }
        });
        nvDrawer.getMenu().findItem(tvshowLastDrawerItemId).setChecked(true);
    }


    public void selectDrawerItem(MenuItem menuItem, boolean movie) {
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
            case ON_AIR_ID:
                query = Filters.on_the_air.toString();
                setTitle(R.string.on_air);
                break;
            case UPCOMING_ID:
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
        if (movie) {
            movieLastDrawerItemId = menuItem.getItemId();
        } else {
            tvshowLastDrawerItemId = menuItem.getItemId();
        }
        loadInformationByFilter(movie);
        mDrawer.closeDrawers();
    }

    private void loadInformationByFilter(boolean movie) {
        if (movie) {
            if (!query.equals(Filters.custom.toString())) {
                movieListFragment.setCallOptionsVisibility(View.GONE);
                movieListFragment.loadMediaInfoByFilter(query, language, "1");
            } else {
                movieListFragment.setCallOptionsVisibility(View.VISIBLE);
                movieListFragment.loadMediaInfoByCustomFilter(language, "1");
            }
        } else {
            if (!query.equals(Filters.custom.toString())) {
                tvShowListFragment.setCallOptionsVisibility(View.GONE);
                tvShowListFragment.loadMediaInfoByFilter(query, language, "1");
            } else {
                tvShowListFragment.setCallOptionsVisibility(View.VISIBLE);
                tvShowListFragment.loadMediaInfoByCustomFilter(language, "1");
            }
        }
    }

    private void loadInformationBySearchQuery(boolean movie) {
        if (movie) {
            movieListFragment.loadMediaByName(query, language, "1");
        } else {
            tvShowListFragment.loadMediaByName(query, language, "1");
        }
    }

    private void searchByName(String name, boolean movie) {
        query = name;
        setTitle("Search results");
        loadInformationBySearchQuery(movie);
    }

    public void loadInformationByGenreIds(String genreIds, boolean movie) {
        if (movie) {
            movieListFragment.loadMediaInfoByIds(genreIds, language, "1", Tasks.SEARCH_BY_GENRE);
        } else {
            tvShowListFragment.loadMediaInfoByIds(genreIds, language, "1", Tasks.SEARCH_BY_GENRE);
        }
        query = genreIds;
    }

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
