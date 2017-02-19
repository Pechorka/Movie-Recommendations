package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.fragments.MovieInfoFragment;
import ru.surf.course.movierecommendations.fragments.MovieReviewsFragment;
import ru.surf.course.movierecommendations.fragments.TvShowInfoFragment;
import ru.surf.course.movierecommendations.models.TVShowInfo;

/**
 * Created by andrew on 2/19/17.
 */
public class TVShowInfosPagerAdapter extends FragmentStatePagerAdapter{

    private static final int PAGE_COUNT = 2;
    private Context mContext;
    private TVShowInfo mTVShowInfo;

    public TVShowInfosPagerAdapter(FragmentManager fm, Context context, TVShowInfo movieInfo) {
        super(fm);
        mContext = context;
        mTVShowInfo = movieInfo;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TvShowInfoFragment.newInstance(mTVShowInfo);
            case 1:
                return MovieReviewsFragment.newInstance(381288);
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
                return mContext.getResources().getString(R.string.info);
            case 1:
                return mContext.getResources().getString(R.string.reviews);
        }
        return null;
    }
    
}
