package ru.surf.course.movierecommendations.ui.screen.tvShow.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.ui.screen.tvShowInfo.TvShowInfoFragmentView;
import ru.surf.course.movierecommendations.ui.screen.tvShowSeasons.TvShowSeasonsFragmentView;

public class TVShowInfosPagerAdapter extends FragmentStatePagerAdapter {

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
                return TvShowInfoFragmentView.newInstance(mTVShowInfo);
            case 1:
                return TvShowSeasonsFragmentView.newInstance(mTVShowInfo);
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
                return mContext.getResources().getString(R.string.seasons);
        }
        return null;
    }

}
