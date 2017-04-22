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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetPersonTaskRetrofit;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonInfoFragment extends Fragment {

  final static String KEY_PERSON = "person";
  final static String KEY_PERSON_ID = "person_id";
  final static int DATA_TO_LOAD = 1;
  final String LOG_TAG = getClass().getSimpleName();

  private ProgressBar progressBar;
  private ExpandableTextView biography;
  private Button expandCollapseBiographyButton;
  private Person currentPerson;
  private Person currentPersonEnglish;
  private TextView gender;
  private TextView placeOfBirth;
  private TextView popularity;

  private int dataLoaded = 0;

  public static PersonInfoFragment newInstance(
      Person person) {  //considering this object already has all info
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

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    dataLoaded = 0;
    if (getArguments().containsKey(KEY_PERSON)) {
      currentPerson = (Person) getArguments().getSerializable(KEY_PERSON);
      dataLoadComplete();
    } else if (getArguments().containsKey(KEY_PERSON_ID)) {
      currentPerson = new Person(getArguments().getInt(KEY_PERSON_ID));
      loadInformationInto(currentPerson, Utilities.getSystemLanguage());
    }
  }

  private void loadInformationInto(final Person person, String language) {
    RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory
        .createWithScheduler(Schedulers.io());
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
          DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

          @Override
          public Date deserialize(final JsonElement json, final Type typeOfT,
              final JsonDeserializationContext context)
              throws JsonParseException {
            try {
              return df.parse(json.getAsString());
            } catch (ParseException e) {
              return null;
            }
          }
        }).create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(rxAdapter)
        .build();
    GetPersonTaskRetrofit getPersonTaskRetrofit = retrofit.create(GetPersonTaskRetrofit.class);
    Observable<Person> call = getPersonTaskRetrofit
        .getPersonById(person.getId(), BuildConfig.TMDB_API_KEY, language);
    call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(person1 -> {
          person.fillFields(person1);
          dataLoadComplete();
        });
  }

  private boolean checkInformation(Person person) {
    return Utilities.checkString(person.getBiography()) &&
        Utilities.checkString(person.getPlaceOfBirth());
    //for now checking only biography
  }

  private String firstLetterToUpper(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }


  private void fillInformation() {

    if (Utilities.checkString(currentPerson.getBiography())) {
      biography.setText(currentPerson.getBiography());
    } else {
      biography.setText(currentPersonEnglish.getBiography());
    }

    biography.post(() -> {
      if (biography.getLineCount() >= biography.getMaxLines()) {
        expandCollapseBiographyButton.setVisibility(View.VISIBLE);
      }
    });

    if (currentPerson.getGender() != null) {
      gender.setText(
          getResources().getStringArray(R.array.genders)[currentPerson.getGender().ordinal()]);
    }
    if (Utilities.checkString(currentPerson.getPlaceOfBirth())) {
      placeOfBirth.setText(currentPerson.getPlaceOfBirth());
    } else if (Utilities.checkString(currentPersonEnglish.getPlaceOfBirth())) {
      placeOfBirth.setText(currentPersonEnglish.getPlaceOfBirth());
    }

    popularity.setText(String.valueOf(currentPerson.getPopularity()));
  }

  private void dataLoadComplete() {
    if (++dataLoaded == DATA_TO_LOAD) {
      if (!checkInformation(currentPerson) && currentPersonEnglish == null) {
        dataLoaded--;
        currentPersonEnglish = new Person(currentPerson.getId());
        loadInformationInto(currentPersonEnglish, Locale.ENGLISH.getLanguage());
      } else {
        fillInformation();
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
    Log.v(LOG_TAG, "data loaded:" + dataLoaded);
  }

}
