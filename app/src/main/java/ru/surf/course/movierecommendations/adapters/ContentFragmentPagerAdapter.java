package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import ru.surf.course.movierecommendations.fragments.MoviesListFragment;
import ru.surf.course.movierecommendations.tmdbTasks.Filters;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by Sergey on 09.02.2017.
 */

public class ContentFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private Context context;
    private String filter;

    public ContentFragmentPagerAdapter(FragmentManager fm, Context context, Filters filter) {
        super(fm);
        this.context = context;
        this.filter = filter.toString();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MoviesListFragment.newInstance(filter, Locale.getDefault().getLanguage(), Tasks.SEARCH_BY_FILTER, true);
            case 1:
                return MoviesListFragment.newInstance(filter, Locale.getDefault().getLanguage(), Tasks.SEARCH_BY_FILTER, false);
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
