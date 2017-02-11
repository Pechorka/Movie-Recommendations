package ru.surf.course.movierecommendations.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import ru.surf.course.movierecommendations.AppBarStateChangeListener;
import ru.surf.course.movierecommendations.MainActivity;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.adapters.PersonInfosPagerAdapter;
import ru.surf.course.movierecommendations.models.Person;
import ru.surf.course.movierecommendations.models.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetPersonsTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

/**
 * Created by andrew on 2/9/17.
 */

public class PersonFactsFragment extends Fragment {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";
    final static int DATA_TO_LOAD = 1;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private ExpandableTextView biography;
    private Button expandCollapseBiographyButton;
    private Person currentPerson;
    private Person currentPersonEnglish;

    private int dataLoaded = 0;

    public static PersonFactsFragment newInstance(Person person) {  //considering this object already has all info
        PersonFactsFragment personFactsFragment = new PersonFactsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PERSON, person);
        personFactsFragment.setArguments(bundle);
        return personFactsFragment;
    }

    public static PersonFactsFragment newInstance(int personId) {
        PersonFactsFragment personFactsFragment = new PersonFactsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERSON_ID, personId);
        personFactsFragment.setArguments(bundle);
        return personFactsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_person_facts, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root){
        progressBar = (ProgressBar) root.findViewById(R.id.person_facts_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
        biography = (ExpandableTextView) root.findViewById(R.id.person_facts_biography);
        expandCollapseBiographyButton = (Button)root.findViewById(R.id.person_facts_biography_expand_btn);
    }

    private void setupViews(View root) {
        biography.setInterpolator(new AccelerateDecelerateInterpolator());

        View.OnClickListener expandCollapse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biography.toggle();
                expandCollapseBiographyButton.setBackground(biography.isExpanded() ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
            }
        };
        expandCollapseBiographyButton.setOnClickListener(expandCollapse);
        biography.setOnClickListener(expandCollapse);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dataLoaded = 0;
        if (getArguments().containsKey(KEY_PERSON)) {
            currentPerson = (Person) getArguments().getSerializable(KEY_PERSON);
            dataLoadComplete();
        } else if (getArguments().containsKey(KEY_PERSON_ID)) {
            currentPerson = new Person(getArguments().getInt(KEY_PERSON_ID));
            loadInformationInto(currentPerson, getCurrentLocale().getLanguage());
        }
    }

    private void loadInformationInto(final Person person, String language) {
        GetPersonsTask getPersonsTask = new GetPersonsTask();
        getPersonsTask.addListener(new GetPersonsTask.PersonsTaskCompleteListener() {
            @Override
            public void taskCompleted(List<Person> result) {
                if (person != null)
                    person.fillFields(result.get(0));
                dataLoadComplete();
            }

            @Override
            public void error(Exception e) {

            }
        });
        getPersonsTask.getPersonById(person.getId(), new Locale(language));
    }

    private boolean checkInformation(Person person) {
        return Utilities.checkString(person.getBiography());
        //for now checking only biography
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }


    private void fillInformation() {

        if (Utilities.checkString(currentPerson.getBiography()))
            biography.setText(currentPerson.getBiography());
        else biography.setText(currentPersonEnglish.getBiography());

        biography.post(new Runnable() {
            @Override
            public void run() {
                if (biography.getLineCount() >= biography.getMaxLines())
                    expandCollapseBiographyButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentPerson) && currentPersonEnglish == null) {
                dataLoaded--;
                currentPersonEnglish = new Person(currentPerson.getId());
                loadInformationInto(currentPersonEnglish, Locale.ENGLISH.getLanguage());
            }
            else {
                fillInformation();
                View progressBarPlaceholder = null;
                if (getView() != null)
                    progressBarPlaceholder = getView().findViewById(R.id.person_facts_progress_bar_placeholder);
                if (progressBarPlaceholder != null)
                    progressBarPlaceholder.setVisibility(View.GONE);
            }
        }
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    }

    private Locale getCurrentLocale() {
        return Locale.getDefault();
    }

}
