package ru.surf.course.movierecommendations.fragments;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.models.Person;
import ru.surf.course.movierecommendations.tmdbTasks.GetPersonsTask;

/**
 * Created by andrew on 2/3/17.
 */

public class PersonInfoFragment extends Fragment {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";
    final static int DATA_TO_LOAD = 1;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private TextView name;
    private ExpandableTextView biography;
    private Button expandCollapseBiographyButton;
    private TextView birthDate;
    private Person currentPerson;
    private Person currentPersonEnglish;


    private int dataLoaded = 0;

    private String language;

    public static PersonInfoFragment newInstance(Person person) {
        PersonInfoFragment personInfoFragment = new PersonInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PERSON, person);
        personInfoFragment.setArguments(bundle);
        return personInfoFragment;
    }

    public static PersonInfoFragment newInstance(int personId) {
        PersonInfoFragment personInfoFragment = new PersonInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERSON_ID, personId);
        personInfoFragment.setArguments(bundle);
        return personInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_person_info, container, false);
        initViews(root);

        biography.setInterpolator(new OvershootInterpolator());

        View.OnClickListener expandCollapse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biography.toggle();
                expandCollapseBiographyButton.setBackground(biography.isExpanded() ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
            }
        };
        expandCollapseBiographyButton.setOnClickListener(expandCollapse);
        biography.setOnClickListener(expandCollapse);

        language = Locale.getDefault().getLanguage();

        return root;
    }

    private void initViews(View root){
        progressBar = (ProgressBar) root.findViewById(R.id.person_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
        name = (TextView) root.findViewById(R.id.person_info_name);
        biography = (ExpandableTextView) root.findViewById(R.id.person_info_biography);
        expandCollapseBiographyButton = (Button)root.findViewById(R.id.person_info_biography_expand_btn);
        birthDate = (TextView) root.findViewById(R.id.person_info_birth_date);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int id = -1;
        if (getArguments().containsKey(KEY_PERSON)) {
            id = ((Person) getArguments().getSerializable(KEY_PERSON)).getId();
        } else if (getArguments().containsKey(KEY_PERSON_ID)) {
            id = getArguments().getInt(KEY_PERSON_ID);
        }
        dataLoaded = 0;
        loadInformation(id, language);
    }


    public void loadInformation(int personId, String language) {
        GetPersonsTask getPersonsTask = new GetPersonsTask();
        getPersonsTask.addListener(new GetPersonsTask.PersonsTaskCompleteListener() {
            @Override
            public void taskCompleted(List<Person> result) {
                if (result.get(0).getInfoLanguage().getLanguage().equals(Locale.getDefault().getLanguage())) {
                    currentPerson = result.get(0);
                    dataLoadComplete();
                }
                else if (result.get(0).getInfoLanguage().getLanguage().equals("en")){
                    currentPersonEnglish = result.get(0);
                    dataLoadComplete();
                }
            }
        });
        getPersonsTask.getPersonById(personId, new Locale(language));
    }

    private boolean checkInformation(Person person) {
        if (person.getBiography() == null || person.getBiography().equals("") || person.getBiography().equals("null"))
            return false;
        return true;
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }


    public void fillInformation() {
        name.setText(currentPerson.getName());

        if (currentPerson.getBiography() == null || currentPerson.getBiography().equals("") || currentPerson.getBiography().equals("null"))
            biography.setText(currentPersonEnglish.getBiography());
        else biography.setText(currentPerson.getBiography());

        if (biography.getLineCount() >= biography.getMaxLines())
            expandCollapseBiographyButton.setVisibility(View.VISIBLE);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        if (currentPerson.getBirthday() != null) {
            birthDate.setText("(" + dateFormat.format(currentPerson.getBirthday()) + ")");
        }
    }

    public void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentPerson) && currentPersonEnglish == null) {
                dataLoaded = 0;
                loadInformation(currentPerson.getId(), "en");
            }
            else {
                fillInformation();
                View progressBarPlaceholder = null;
                if (getView() != null)
                    progressBarPlaceholder = getView().findViewById(R.id.person_info_progress_bar_placeholder);
                if (progressBarPlaceholder != null)
                    progressBarPlaceholder.setVisibility(View.GONE);
            }
        }
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    }

}
