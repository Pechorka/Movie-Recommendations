package ru.surf.course.movierecommendations.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery_image, container, false);
        image = (ImageView)root.findViewById(R.id.gallery_image);
        if (getArguments().containsKey(PATH_TAG)) {
            ImageLoader.putPoster(getActivity(), getArguments().getString(PATH_TAG), image, ImageLoader.sizes.w780);
        }
        return root;
    }
}
