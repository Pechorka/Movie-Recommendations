package ru.surf.course.movierecommendations;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    public static void start(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static final String TAG_MOVIES_LIST_FRAGMENT = "movie_list_fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        MoviesListFragment moviesListFragment;

        if (savedInstanceState == null) {
            final FragmentManager fragmentManager = getFragmentManager();
             moviesListFragment = MoviesListFragment.newInstance("en", "popular", true);
            fragmentManager.beginTransaction().add(R.id.activity_main_container, moviesListFragment, TAG_MOVIES_LIST_FRAGMENT).commit();
        } else {
            moviesListFragment = (MoviesListFragment)getFragmentManager().findFragmentByTag(TAG_MOVIES_LIST_FRAGMENT);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isInternetOn()) {
                    loadInformation(query.replace(' ', '+'));
                }
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
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


    private boolean isInternetOn() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnectedOrConnecting();
    }

    private void loadInformation(String name) {
        MoviesListFragment fragment = MoviesListFragment.newInstance(Locale.getDefault().getLanguage(), name, true);
        switchContent(R.id.activity_main_container, fragment);
    }

    public void switchContent(int id, Fragment fragment) {
        //noinspection ResourceType
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(id, fragment).addToBackStack(null).commit();
    }
}
