package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.fragments.MovieInfoFragment;
import ru.surf.course.movierecommendations.fragments.MovieReviewsFragment;
import ru.surf.course.movierecommendations.models.MovieInfo;

/**
 * Created by andrew on 2/11/17.
 */

public class MovieInfosPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGE_COUNT = 2;
    private Context mContext;
    private MovieInfo mMovieInfo;

    public MovieInfosPagerAdapter(FragmentManager fm, Context context, MovieInfo movieInfo) {
        super(fm);
        mContext = context;
        mMovieInfo = movieInfo;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MovieInfoFragment.newInstance(mMovieInfo);
            case 1:
                return MovieReviewsFragment.newInstance(mMovieInfo);
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
