package ru.surf.course.movierecommendations.fragments;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import ru.surf.course.movierecommendations.models.Person;
import ru.surf.course.movierecommendations.models.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetPersonsTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

public class PersonInfoFragment extends Fragment {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";
    final static int DATA_TO_LOAD = 2;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private TextView name;
    private ExpandableTextView biography;
    private Button expandCollapseBiographyButton;
    private TextView birthDate;
    private Person currentPerson;
    private Person currentPersonEnglish;
    private ImageView pictureProfile;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    private int dataLoaded = 0;

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

        setActivityToolbarVisibility(false);

        View root = inflater.inflate(R.layout.fragment_person_info, container, false);
        initViews(root);
        setupViews(root);

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
        pictureProfile = (ImageView)root.findViewById(R.id.person_info_backdrop);
        collapsingToolbarLayout = (CollapsingToolbarLayout)root.findViewById(R.id.person_info_collapsing_toolbar_layout);
        toolbar = (Toolbar)root.findViewById(R.id.person_info_toolbar);
        appBarLayout = (AppBarLayout)root.findViewById(R.id.person_info_appbar_layout);
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

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        AppBarStateChangeListener appBarStateChangeListener = new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state.equals(State.COLLAPSED)) {

                } else {

                }

            }
        };
        appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dataLoaded = 0;
        if (getArguments().containsKey(KEY_PERSON)) {
            currentPerson = (Person) getArguments().getSerializable(KEY_PERSON);
        } else if (getArguments().containsKey(KEY_PERSON_ID)) {
            currentPerson = new Person(getArguments().getInt(KEY_PERSON_ID));
        }
        if (currentPerson != null) {
            loadInformationInto(currentPerson, getCurrentLocale().getLanguage());
            loadProfilePicturesInto(currentPerson);
        }
    }

    @Override
    public void onDetach() {
        setActivityToolbarVisibility(true);
        super.onDetach();
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
        });
        getPersonsTask.getPersonById(person.getId(), new Locale(language));
    }

    private void loadProfilePicturesInto(final Person person) {
        GetImagesTask getImagesTask = new GetImagesTask();
        getImagesTask.addListener(new GetImagesTask.TaskCompletedListener() {
            @Override
            public void getImagesTaskCompleted(List<TmdbImage> result) {
                person.setProfilePictures(result);
                dataLoadComplete();
            }
        });
        getImagesTask.getPersonImages(person.getId(), Tasks.GET_PROFILE_PICTURES);
    }

    private boolean checkInformation(Person person) {
        return Utilities.checkString(person.getBiography());
        //for now checking only biography
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }


    private void fillInformation() {
        name.setText(currentPerson.getName());

        if (Utilities.checkString(currentPerson.getBiography()))
            biography.setText(currentPerson.getBiography());
        else biography.setText(currentPersonEnglish.getBiography());

        if (biography.getLineCount() >= biography.getMaxLines())
            expandCollapseBiographyButton.setVisibility(View.VISIBLE);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        if (currentPerson.getBirthday() != null) {
            birthDate.setText("(" + dateFormat.format(currentPerson.getBirthday()) + ")");
        }

        ImageLoader.putPosterNoResize(getActivity(), currentPerson.getProfilePictures().get(0).path, pictureProfile, ImageLoader.sizes.w500);
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
                    progressBarPlaceholder = getView().findViewById(R.id.person_info_progress_bar_placeholder);
                if (progressBarPlaceholder != null)
                    progressBarPlaceholder.setVisibility(View.GONE);
            }
        }
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    }

    private Locale getCurrentLocale() {
        return Locale.getDefault();
    }

    private void setActivityToolbarVisibility(boolean flag) {
        if (getActivity() instanceof MainActivity) {
            if (flag) {
                ((MainActivity)getActivity()).getSupportActionBar().show();
            } else {
                ((MainActivity)getActivity()).getSupportActionBar().hide();
            }
        }
    }

}
