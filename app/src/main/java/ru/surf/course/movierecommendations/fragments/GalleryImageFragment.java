package ru.surf.course.movierecommendations.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 1/10/17.
 */

public class GalleryImageFragment extends Fragment {

    private static final String PATH_TAG = "path";

    public static GalleryImageFragment newInstance(String path) {
        GalleryImageFragment galleryImageFragment = new GalleryImageFragment();
        Bundle args = new Bundle();
        args.putString(PATH_TAG, path);
        galleryImageFragment.setArguments(args);
        return galleryImageFragment;
    }

    ImageView image;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery_image, container, false);
        image = (ImageView)root.findViewById(R.id.gallery_image);
        progressBar = (ProgressBar)root.findViewById(R.id.gallery_fragment_progress_bar);
        if (getArguments().containsKey(PATH_TAG)) {
            loadImage(getArguments().getString(PATH_TAG));
        }
        return root;
    }

    private void loadImage(final String path) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                image.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                loadImage(path);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        ImageLoader.getPosterNoResize(getActivity(), path, target, ImageLoader.sizes.w780);
    }
}
