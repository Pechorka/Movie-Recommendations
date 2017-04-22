package ru.surf.course.movierecommendations.ui.screen.personInfo;

import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;

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
import android.widget.TextView;

import at.blogc.android.views.ExpandableTextView;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.presenter.MvpPresenter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetPersonTaskRetrofit;
import ru.surf.course.movierecommendations.ui.base.fragment.BaseFragmentView;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonInfoFragmentView extends BaseFragmentView {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";

    @Inject
    PersonInfoFragmentPresenter presenter;

    private ProgressBar progressBar;
    private ExpandableTextView biography;
    private Button expandCollapseBiographyButton;
    private TextView gender;
    private TextView placeOfBirth;
    private TextView popularity;

    public static PersonInfoFragmentView newInstance(
            Person person) {  //considering this object already has all info
        PersonInfoFragmentView personInfoFragmentView = new PersonInfoFragmentView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PERSON, person);
        personInfoFragmentView.setArguments(bundle);
        return personInfoFragmentView;
    }

    public static PersonInfoFragmentView newInstance(int personId) {
        PersonInfoFragmentView personInfoFragmentView = new PersonInfoFragmentView();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERSON_ID, personId);
        personInfoFragmentView.setArguments(bundle);
        return personInfoFragmentView;
    }

    @Override
    public MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerPersonInfoFragmentComponent.builder()
                .fragmentModule(getFragmentModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() == null) {
            onDestroy();
        }

        View root = inflater.inflate(R.layout.fragment_person_info, container, false);
        initViews(root);
        setupViews(root);

        return root;
    }

    private void initViews(View root) {
        progressBar = (ProgressBar) root.findViewById(R.id.person_info_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
        }
        biography = (ExpandableTextView) root.findViewById(R.id.person_info_biography);
        expandCollapseBiographyButton = (Button) root
                .findViewById(R.id.person_info_biography_expand_btn);
        gender = (TextView) root.findViewById(R.id.person_info_gender);
        placeOfBirth = (TextView) root.findViewById(R.id.person_info_place_of_birth);
        popularity = (TextView) root.findViewById(R.id.person_info_popularity);
    }

    private void setupViews(View root) {
        biography.setInterpolator(new AccelerateDecelerateInterpolator());

        View.OnClickListener expandCollapse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biography.toggle();
                expandCollapseBiographyButton.setBackground(biography.isExpanded() ? ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_arrow_down)
                        : ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_up));
            }
        };
        expandCollapseBiographyButton.setOnClickListener(expandCollapse);
        biography.setOnClickListener(expandCollapse);
    }


    void fillInformation(Person data, Person fallbackData) {

        if (Utilities.checkString(data.getBiography())) {
            biography.setText(data.getBiography());
        } else {
            biography.setText(fallbackData.getBiography());
        }

        biography.post(() -> {
            if (biography.getLineCount() >= biography.getMaxLines()) {
                expandCollapseBiographyButton.setVisibility(View.VISIBLE);
            }
        });

        if (data.getGender() != null) {
            gender.setText(
                    getResources().getStringArray(R.array.genders)[data.getGender().ordinal()]);
        }
        if (Utilities.checkString(data.getPlaceOfBirth())) {
            placeOfBirth.setText(data.getPlaceOfBirth());
        } else if (Utilities.checkString(fallbackData.getPlaceOfBirth())) {
            placeOfBirth.setText(fallbackData.getPlaceOfBirth());
        }

        popularity.setText(String.valueOf(data.getPopularity()));
    }

    void hideProgressBar() {
        View progressBarPlaceholder = null;
        if (getView() != null) {
            progressBarPlaceholder = getView()
                    .findViewById(R.id.person_info_progress_bar_placeholder);
        }
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

}
