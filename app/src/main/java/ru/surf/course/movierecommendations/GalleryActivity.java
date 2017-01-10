package ru.surf.course.movierecommendations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ru.surf.course.movierecommendations.Adapters.GalleryPagerAdapter;
import ru.surf.course.movierecommendations.models.TmdbImage;

/**
 * Created by andrew on 1/8/17.
 */

public class GalleryActivity extends AppCompatActivity {

    public static final String IMAGES_TAG = "images";
    public static final String INIT_POSITION_TAG = "init_position";

    public static void start(Context context, ArrayList<String> paths) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(IMAGES_TAG, paths);
        context.startActivity(intent);
    }

    public static void start(Context context, ArrayList<String> paths, int initPosition) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(IMAGES_TAG, paths);
        intent.putExtra(INIT_POSITION_TAG, initPosition);
        context.startActivity(intent);
    }

    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        viewPager = (ViewPager)findViewById(R.id.gallery_activity_view_pager);

        if (getIntent().hasExtra(IMAGES_TAG)) {
            ArrayList<String> paths = (ArrayList<String>) getIntent().getSerializableExtra(IMAGES_TAG);
            GalleryPagerAdapter adapter = new GalleryPagerAdapter(getSupportFragmentManager(),paths);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(1);
            if (getIntent().hasExtra(INIT_POSITION_TAG))
                viewPager.setCurrentItem(getIntent().getIntExtra(INIT_POSITION_TAG, 0));
        }
    }
}
