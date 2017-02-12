package ru.surf.course.movierecommendations.fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.activities.GalleryActivity;
import ru.surf.course.movierecommendations.activities.MainActivity;
import ru.surf.course.movierecommendations.adapters.PersonInfosPagerAdapter;
import ru.surf.course.movierecommendations.models.Person;
import ru.surf.course.movierecommendations.models.TmdbImage;
import ru.surf.course.movierecommendations.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.tmdbTasks.GetPersonsTask;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.tmdbTasks.Tasks;

public class PersonFragment extends Fragment {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";
    final static int DATA_TO_LOAD = 2;
    final String LOG_TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private TextView name;
    private TextView birthDate;
    private Person currentPerson;
    private ImageView pictureProfile;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ViewPager infosPager;
    private View fakeStatusBar;
    private Button backButton;

    private int dataLoaded = 0;

    public static PersonFragment newInstance(Person person) {
        PersonFragment personFragment = new PersonFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PERSON, person);
        personFragment.setArguments(bundle);
        return personFragment;
    }

    public static PersonFragment newInstance(int personId) {
        PersonFragment personFragment = new PersonFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERSON_ID, personId);
        personFragment.setArguments(bundle);
        return personFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).setDrawerEnabled(false);
        setStatusBarTranslucent(true);
        setActivityToolbarVisibility(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null)
            onDestroy();

        View root = inflater.inflate(R.layout.fragment_person, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.person_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
        name = (TextView) root.findViewById(R.id.person_name);
        birthDate = (TextView) root.findViewById(R.id.person_birth_date);
        pictureProfile = (ImageView) root.findViewById(R.id.person_backdrop);
        collapsingToolbarLayout = (CollapsingToolbarLayout) root.findViewById(R.id.person_collapsing_toolbar_layout);
        appBarLayout = (AppBarLayout) root.findViewById(R.id.person_appbar_layout);
        infosPager = (ViewPager) root.findViewById(R.id.person_infos_pager);
        fakeStatusBar = root.findViewById(R.id.person_fake_status_bar);
        backButton = (Button)root.findViewById(R.id.person_back_button);
    }

    private void setupViews(View root) {
       backButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getActivity().onBackPressed();
           }
       });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                verticalOffset = Math.abs(verticalOffset);
                int offsetToToolbar = appBarLayout.getTotalScrollRange() - Utilities.getActionBarHeight(getActivity()) - fakeStatusBar.getMeasuredHeight();
                if (verticalOffset >= offsetToToolbar) {
                    fakeStatusBar.setAlpha((float)(verticalOffset-offsetToToolbar)/Utilities.getActionBarHeight(getActivity()));
                }
            }
        });
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

        if (Build.VERSION.SDK_INT >= 19) {
            fakeStatusBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDetach() {
        if (getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).setDrawerEnabled(true);
        fakeStatusBar.setVisibility(View.GONE);
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
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    private void fillInformation() {
        name.setText(currentPerson.getName());

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        if (currentPerson.getBirthday() != null) {
            birthDate.setText("(" + dateFormat.format(currentPerson.getBirthday()) + ")");
        }

        if (currentPerson.getProfilePictures() != null && currentPerson.getProfilePictures().size() != 0) {
            ImageLoader.putPosterNoResize(getActivity(), currentPerson.getProfilePictures().get(0).path, pictureProfile, ImageLoader.sizes.w500);
            pictureProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> paths = new ArrayList<String>();
                    for (TmdbImage image:
                           currentPerson.getProfilePictures()) {
                        paths.add(image.path);
                    }
                    GalleryActivity.start(getActivity(), paths);
                }
            });
        }
        PersonInfosPagerAdapter personInfosPagerAdapter = new PersonInfosPagerAdapter(this.getChildFragmentManager(), getActivity(), currentPerson);
        infosPager.setAdapter(personInfosPagerAdapter);
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Log.v(LOG_TAG, "data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            fillInformation();
            View progressBarPlaceholder = null;
            if (getView() != null)
                progressBarPlaceholder = getView().findViewById(R.id.person_progress_bar_placeholder);
            if (progressBarPlaceholder != null)
                progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

    private Locale getCurrentLocale() {
        return Locale.getDefault();
    }

    private void setActivityToolbarVisibility(boolean flag) {
        if (getActivity() instanceof MainActivity) {
            if (flag) {
                ((MainActivity) getActivity()).getSupportActionBar().show();
            } else {
                ((MainActivity) getActivity()).getSupportActionBar().hide();
            }
        }
    }

    private void setStatusBarTranslucent(boolean flag) {
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getActivity().getWindow();
            if (flag)
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }


}
