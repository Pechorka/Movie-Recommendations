package ru.surf.course.movierecommendations.ui.screen.movie.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.ui.screen.movieInfo.MovieInfoFragmentView;
import ru.surf.course.movierecommendations.ui.screen.movieReviews.MovieReviewsFragmentView;


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
                return MovieInfoFragmentView.newInstance(mMovieInfo);
            case 1:
                return MovieReviewsFragmentView.newInstance(mMovieInfo);
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
