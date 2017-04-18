package ru.surf.course.movierecommendations.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ru.surf.course.movierecommendations.ui.fragments.MediaListFragment;

/**
 * Created by Sergey on 09.02.2017.
 */

public class ContentFragmentPagerAdapter extends FragmentPagerAdapter {

  final int PAGE_COUNT = 2;
  private Context context;
  private String filter;
  private MediaListFragment movieListFragment;
  private MediaListFragment tvShowListFragment;

  public ContentFragmentPagerAdapter(FragmentManager fm, Context context, String filter
      , MediaListFragment movieListFragment, MediaListFragment tvShowListFragment) {
    super(fm);
    this.context = context;
    this.filter = filter;
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
