package ru.surf.course.movierecommendations.ui.screen.personInfo;

import static ru.surf.course.movierecommendations.ui.screen.personInfo.PersonInfoFragmentView.KEY_PERSON;
import static ru.surf.course.movierecommendations.ui.screen.personInfo.PersonInfoFragmentView.KEY_PERSON_ID;

import com.agna.ferro.mvp.component.scope.PerScreen;
import java.util.Locale;
import javax.inject.Inject;
import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetPersonTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;
import rx.Observable;

@PerScreen
public class PersonInfoFragmentPresenter extends BasePresenter<PersonInfoFragmentView> {

    private final static int DATA_TO_LOAD = 1;
    private int dataLoaded = 0;

    private Retrofit retrofit;

    private Person currentPerson;
    private Person currentPersonEnglish;

    @Inject
    public PersonInfoFragmentPresenter(ErrorHandler errorHandler, Retrofit retrofit) {
        super(errorHandler);
        this.retrofit = retrofit;
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        dataLoaded = 0;
        if (getView().getArguments().containsKey(KEY_PERSON)) {
            currentPerson = (Person) getView().getArguments().getSerializable(KEY_PERSON);
            dataLoadComplete();
        } else if (getView().getArguments().containsKey(KEY_PERSON_ID)) {
            currentPerson = new Person(getView().getArguments().getInt(KEY_PERSON_ID));
            loadInformationInto(currentPerson, Utilities.getSystemLanguage());
        }
    }

    private void loadInformationInto(final Person person, String language) {
        GetPersonTask getPersonTask = retrofit.create(GetPersonTask.class);
        Observable<Person> call = getPersonTask
                .getPersonById(person.getId(), BuildConfig.TMDB_API_KEY, language);
        subscribeNetworkQuery(call, person1 -> {
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

    private void dataLoadComplete() {
        if (++dataLoaded == DATA_TO_LOAD) {
            if (!checkInformation(currentPerson) && currentPersonEnglish == null) {
                dataLoaded--;
                currentPersonEnglish = new Person(currentPerson.getId());
                loadInformationInto(currentPersonEnglish, Locale.ENGLISH.getLanguage());
            } else {
                getView().fillInformation(currentPerson, currentPersonEnglish);
                getView().hideProgressBar();
            }
        }
        Logger.d("data loaded:" + dataLoaded);
    }
}
