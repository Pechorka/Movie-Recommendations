package ru.surf.course.movierecommendations.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.models.Person;
import ru.surf.course.movierecommendations.tmdbTasks.GetPersonsTask;

/**
 * Created by andrew on 2/9/17.
 */

public class PersonCreditsFragment extends Fragment {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";
    final static int DATA_TO_LOAD = 1;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private Person currentPerson;

    private int dataLoaded = 0;

    public static PersonCreditsFragment newInstance(Person person) {
        PersonCreditsFragment personCreditsFragment = new PersonCreditsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PERSON, person);
        personCreditsFragment.setArguments(bundle);
        return personCreditsFragment;
    }

    public static PersonCreditsFragment newInstance(int personId) {
        PersonCreditsFragment personCreditsFragment = new PersonCreditsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERSON_ID, personId);
        personCreditsFragment.setArguments(bundle);
        return personCreditsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_person_credits, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.person_credits_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
    }

    private void setupViews(View root) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dataLoaded = 0;
        if (getArguments().containsKey(KEY_PERSON)) {

        } else if (getArguments().containsKey(KEY_PERSON_ID)) {

        }
    }


    private void fillInformation() {

    }

    private void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {

            fillInformation();
            View progressBarPlaceholder = null;
            if (getView() != null)
                progressBarPlaceholder = getView().findViewById(R.id.person_credits_progress_bar_placeholder);
            if (progressBarPlaceholder != null)
                progressBarPlaceholder.setVisibility(View.GONE);

        }
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    }

    private Locale getCurrentLocale() {
        return Locale.getDefault();
    }

}
