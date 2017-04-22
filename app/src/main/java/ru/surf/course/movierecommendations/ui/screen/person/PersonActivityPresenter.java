package ru.surf.course.movierecommendations.ui.screen.person;


import com.agna.ferro.mvp.component.scope.PerScreen;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.TmdbImage;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.interactor.network.connection.NetworkConnectionChecker;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetImagesTask;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetPersonTaskRetrofit;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;

import static ru.surf.course.movierecommendations.ui.screen.person.PersonActivityView.KEY_PERSON;
import static ru.surf.course.movierecommendations.ui.screen.person.PersonActivityView.KEY_PERSON_ID;

@PerScreen
public class PersonActivityPresenter extends BasePresenter<PersonActivityView> {

    private final static int DATA_TO_LOAD = 2;

    private int dataLoaded = 0;

    private Person currentPerson;

    private Retrofit retrofit;
    private String apiKey;
    private NetworkConnectionChecker connectionChecker;

    @Inject
    public PersonActivityPresenter(ErrorHandler errorHandler,
                                   NetworkConnectionChecker connectionChecker,
                                   Retrofit retrofit) {
        super(errorHandler);
        this.connectionChecker = connectionChecker;
        this.retrofit = retrofit;
    }

    @Override
    public void onLoad(boolean viewRecreated) {
        super.onLoad(viewRecreated);

        init();
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        if (connectionChecker.hasInternetConnection()) {
            startLoading();
        } else {
            getView().showErrorPlaceholder();
        }
    }

    private void init() {
        apiKey = BuildConfig.TMDB_API_KEY;
    }

    private void startLoading() {
        dataLoaded = 0;
        if (getView().getIntent().hasExtra(KEY_PERSON)) {
            currentPerson = (Person) getView().getIntent().getSerializableExtra(KEY_PERSON);
        } else if (getView().getIntent().hasExtra(KEY_PERSON_ID)) {
            currentPerson = new Person(getView().getIntent().getIntExtra(KEY_PERSON_ID, -1));
        }
        if (currentPerson != null) {
            loadInformationInto(currentPerson, Utilities.getSystemLanguage());
            loadProfilePicturesInto(currentPerson);
        }

        getView().showFakeStatusBar();
    }

    private void loadInformationInto(final Person person, String language) {
        GetPersonTaskRetrofit getPersonTaskRetrofit = retrofit.create(GetPersonTaskRetrofit.class);
        Observable<Person> call = getPersonTaskRetrofit.getPersonById(person.getId(), apiKey, language);
        subscribeNetworkQuery(call, person1 -> {
            person.fillFields(person1);
            dataLoadComplete();
        });
    }

    private void loadProfilePicturesInto(final Person person) {
        GetImagesTask getImagesTask = retrofit.create(GetImagesTask.class);
        Observable<TmdbImage.RetrofitResultProfiles> call = getImagesTask
                .getProfilePictures(person.getId(), apiKey);
        subscribeNetworkQuery(call, tmdbImages -> {
            person.setProfilePictures(tmdbImages.profilePictures);
            dataLoadComplete();
        });
    }

    private boolean checkInformation(Person person) {
        return Utilities.checkString(person.getBiography());
        //for now checking only biography
    }

    private String firstLetterToUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Logger.d("data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            getView().fillInformation(currentPerson);
            getView().hideProgressBar();
        }
    }

    void onRetryBtnClick() {
        if (connectionChecker.hasInternetConnection()) {
            getView().hideErrorPlaceholder();
            startLoading();
        }
    }
}
