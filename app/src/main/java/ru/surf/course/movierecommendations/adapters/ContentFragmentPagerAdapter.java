package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.surf.course.movierecommendations.fragments.MovieListFragment;
import ru.surf.course.movierecommendations.fragments.TVShowListFragment;
import ru.surf.course.movierecommendations.tmdbTasks.Filters;

/**
 * Created by Sergey on 09.02.2017.
 */

public class ContentFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private Context context;
    private String filter;
    private MovieListFragment movieListFragment;
    private TVShowListFragment tvShowListFragment;

    public ContentFragmentPagerAdapter(FragmentManager fm, Context context, Filters filter
            , MovieListFragment movieListFragment, TVShowListFragment tvShowListFragment) {
        super(fm);
        this.context = context;
        this.filter = filter.toString();
        this.movieListFragment = movieListFragment;
        this.tvShowListFragment = tvShowListFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return movieListFragment;
            case 1:
                return tvShowListFragment;
        }
        return null;
    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Movies";
            case 1:
                return "TV Shows";
        }
        return null;
    }
}
