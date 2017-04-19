package ru.surf.course.movierecommendations.ui.screen.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.agna.ferro.mvp.component.ScreenComponent;

import java.util.ArrayList;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.screen.gallery.adapters.GalleryPagerAdapter;

public class GalleryActivityView extends BaseActivityView {

    @Inject
    GalleryActivityPresenter presenter;

    public static final String IMAGES_TAG = "images";
    public static final String INIT_POSITION_TAG = "init_position";
    ViewPager viewPager;

    public static void start(Context context, ArrayList<String> paths) {
        Intent intent = new Intent(context, GalleryActivityView.class);
        intent.putExtra(IMAGES_TAG, paths);
        context.startActivity(intent);
    }

    public static void start(Context context, ArrayList<String> paths, int initPosition) {
        Intent intent = new Intent(context, GalleryActivityView.class);
        intent.putExtra(IMAGES_TAG, paths);
        intent.putExtra(INIT_POSITION_TAG, initPosition);
        context.startActivity(intent);
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gallery;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerGalleryActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);

        init();
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.gallery_activity_view_pager);
    }

    public void initImagesList(ArrayList<String> paths) {
        GalleryPagerAdapter adapter = new GalleryPagerAdapter(getSupportFragmentManager(), paths);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    public void setListPosition(int position) {
        viewPager.setCurrentItem(position);
    }
}
