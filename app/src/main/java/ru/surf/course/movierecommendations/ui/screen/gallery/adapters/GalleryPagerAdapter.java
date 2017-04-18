package ru.surf.course.movierecommendations.ui.screen.gallery.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;

import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryImageFragment;

/**
 * Created by andrew on 1/10/17.
 */

public class GalleryPagerAdapter extends FragmentStatePagerAdapter {

  ArrayList<String> mPaths;

  public GalleryPagerAdapter(FragmentManager fm, ArrayList<String> paths) {
    super(fm);
    mPaths = paths;
  }

  @Override
  public int getCount() {
    return mPaths.size();
  }

  @Override
  public Fragment getItem(int position) {
    return GalleryImageFragment.newInstance(mPaths.get(position));
  }
}
