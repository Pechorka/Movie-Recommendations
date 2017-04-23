package ru.surf.course.movierecommendations.ui.screen.main;


import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_GENRE_IDS;
import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_GENRE_NAME;
import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_MEDIA;

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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.agna.ferro.mvp.component.ScreenComponent;
import java.util.List;
import javax.inject.Inject;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.interactor.CustomFilter;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.screen.main.adapters.ContentFragmentPagerAdapter;
import ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView;

public class MainActivityView extends BaseActivityView {

    private static final String LOG_TAG = MainActivityView.class.getSimpleName();
    @Inject
    MainActivityPresenter presenter;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View errorPlaceholder;


    public static void start(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static void start(Context context, Class c, String ids, String genreName, Media.MediaType mediaType) {
        Intent intent = new Intent(context, c);
        intent.putExtra(KEY_GENRE_IDS, ids);
        intent.putExtra(KEY_MEDIA, mediaType);
        intent.putExtra(KEY_GENRE_NAME, genreName);
        context.startActivity(intent);
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerMainActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);

        init();
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
                searchView.clearFocus();
                return presenter.onQueryTextSubmit(query);
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

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showNoInternetMessage() {
        errorPlaceholder = findViewById(R.id.main_no_internet_screen);
        errorPlaceholder.setVisibility(View.VISIBLE);
        (findViewById(R.id.message_no_internet_button))
                .setOnClickListener(view -> presenter.onRetryClick());
    }

    public void hideNoInternetMessage() {
        errorPlaceholder.setVisibility(View.GONE);
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        mDrawer.addDrawerListener(drawerToggle);
        //?????
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.setDrawerIndicatorEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                presenter.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nvDrawer
                .setNavigationItemSelectedListener(item -> {
                    presenter.selectDrawerItem(item);
                    return true;
                });
    }

    public void initViewPager(String query, MediaListFragmentView movieListFragment, MediaListFragmentView tvListFragment) {
        viewPager.setAdapter(new ContentFragmentPagerAdapter(getSupportFragmentManager(),
                this, query, movieListFragment, tvListFragment));
    }

    public void setDrawerItemChecked(int itemId, boolean checked) {
        nvDrawer.getMenu().findItem(itemId).setChecked(checked);
    }


    public void setDrawerEnabled(boolean enabled) {
        if (mDrawer != null) {
            int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
            mDrawer.setDrawerLockMode(lockMode);
            drawerToggle.setDrawerIndicatorEnabled(enabled);
        }
    }

    public void setPresetsSubMenu(List<CustomFilter> customFilters) {
        MenuItem item = nvDrawer.getMenu().findItem(R.id.nam_custom_presets);
        item.getSubMenu().clear();
        Menu menu = item.getSubMenu();
        for (CustomFilter filter : customFilters) {
            MenuItem addedItem = menu.add(filter.getFilterName());
            addedItem.setIcon(R.drawable.ic_preset_icon);
            addedItem.setOnMenuItemClickListener(item1 ->
                    presenter.onPresetItemSelected(item1, filter));
        }
    }

    public void closeDrawer() {
        mDrawer.closeDrawers();
    }

    public void switchDrawer(final Media.MediaType mediaType) {
        MenuItem menuItem = nvDrawer.getMenu().findItem(R.id.nav_upcoming_or_on_air);
        switch (mediaType) {
            case movie:
                menuItem.setIcon(R.drawable.upcoming_icon);
                menuItem.setTitle(R.string.upcoming);
                break;
            case tv:
                menuItem.setIcon(R.drawable.on_air_icon);
                menuItem.setTitle(R.string.on_air);
                break;
        }
        nvDrawer
                .setNavigationItemSelectedListener(item -> {
                    presenter.selectDrawerItem(item);
                    return true;
                });
    }

    public void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private Media.MediaType getCurrentTab() {
        switch (tabLayout.getSelectedTabPosition()) {
            case 1:
                return Media.MediaType.tv;
            case 0:
            default:
                return Media.MediaType.movie;

        }
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
