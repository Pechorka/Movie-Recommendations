package ru.surf.course.movierecommendations.ui.screen.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.surf.course.movierecommendations.ui.screen.mediaList.MediaListFragmentView;


public class ContentFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private MediaListFragmentView movieListFragment;
    private MediaListFragmentView tvShowListFragment;

    public ContentFragmentPagerAdapter(FragmentManager fm, Context context, String filter
            , MediaListFragmentView movieListFragment, MediaListFragmentView tvShowListFragment) {
        super(fm);
        Context context1 = context;
        String filter1 = filter;
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
