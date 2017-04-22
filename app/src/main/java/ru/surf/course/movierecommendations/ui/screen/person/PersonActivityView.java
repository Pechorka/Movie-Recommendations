package ru.surf.course.movierecommendations.ui.screen.person;

import static ru.surf.course.movierecommendations.interactor.common.network.ServerUrls.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agna.ferro.mvp.component.ScreenComponent;
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
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.TmdbImage.RetrofitResultProfiles;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetPersonTaskRetrofit;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.screen.gallery.GalleryActivityView;
import ru.surf.course.movierecommendations.ui.screen.person.adapters.PersonInfosPagerAdapter;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PersonActivityView extends BaseActivityView {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";

    @Inject
    PersonActivityPresenter presenter;

    private ProgressBar progressBar;
    private TextView name;
    private TextView birthDate;
    private ImageView pictureProfile;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ViewPager infosPager;
    private View fakeStatusBar;
    private Toolbar toolbar;
    private View errorPlaceholder;


    public static void start(Context context, Person person) {
        Intent intent = new Intent(context, PersonActivityView.class);
        intent.putExtra(KEY_PERSON, person);
        context.startActivity(intent);
    }

    public static void start(Context context, int personId) {
        Intent intent = new Intent(context, PersonActivityView.class);
        intent.putExtra(KEY_PERSON_ID, personId);
        context.startActivity(intent);
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_person;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerPersonActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);

        if (!getIntent().hasExtra(KEY_PERSON) && !getIntent().hasExtra(KEY_PERSON_ID)) {
            onDestroy();
        }

        initViews();
        setupViews();

    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.person_progress_bar);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
        }
        name = (TextView) findViewById(R.id.person_name);
        birthDate = (TextView) findViewById(R.id.person_birth_date);
        pictureProfile = (ImageView) findViewById(R.id.person_backdrop);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
                R.id.person_collapsing_toolbar_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.person_appbar_layout);
        infosPager = (ViewPager) findViewById(R.id.person_infos_pager);
        fakeStatusBar = findViewById(R.id.person_fake_status_bar);
        toolbar = (Toolbar) findViewById(R.id.person_toolbar);
        errorPlaceholder = findViewById(R.id.person_no_internet_screen);
    }

    private void setupViews() {
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            verticalOffset = Math.abs(verticalOffset);
            int offsetToToolbar =
                    appBarLayout1.getTotalScrollRange() - Utilities.getActionBarHeight(PersonActivityView.this)
                            - fakeStatusBar.getMeasuredHeight();
            if (verticalOffset >= offsetToToolbar) {
                fakeStatusBar.setAlpha((float) (verticalOffset - offsetToToolbar) / Utilities
                        .getActionBarHeight(PersonActivityView.this));
            }
        });

        findViewById(R.id.message_no_internet_button)
                .setOnClickListener(view -> {
                    presenter.onRetryBtnClick();
                });
    }


    void fillInformation(Person data) {
        name.setText(data.getName());

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);

        if (data.getBirthday() != null) {
            birthDate.setText("(" + dateFormat.format(data.getBirthday()) + ")");
        }

        if (data.getProfilePictures() != null
                && data.getProfilePictures().size() != 0) {
            ImageLoader
                    .putPosterNoResize(this, data.getProfilePictures().get(0).path, pictureProfile,
                            ImageLoader.sizes.w500);
            pictureProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> paths = new ArrayList<String>();
                    for (TmdbImage image :
                            data.getProfilePictures()) {
                        paths.add(image.path);
                    }
                    GalleryActivityView.start(PersonActivityView.this, paths);
                }
            });
        }
        PersonInfosPagerAdapter personInfosPagerAdapter = new PersonInfosPagerAdapter(
                getSupportFragmentManager(), this, data);
        infosPager.setAdapter(personInfosPagerAdapter);
    }

    void hideProgressBar() {
        View progressBarPlaceholder = null;
        progressBarPlaceholder = findViewById(R.id.person_progress_bar_placeholder);
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

    void showFakeStatusBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            fakeStatusBar.setVisibility(View.VISIBLE);
        }
    }

    void showErrorPlaceholder() {
        errorPlaceholder.setVisibility(View.VISIBLE);
    }

    void hideErrorPlaceholder() {
        errorPlaceholder.setVisibility(View.GONE);
    }

}
