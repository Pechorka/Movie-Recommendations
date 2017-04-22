package ru.surf.course.movierecommendations.ui.screen.personCredits;

import android.util.Log;
import android.view.View;

import com.agna.ferro.mvp.component.scope.PerScreen;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetCreditsTask;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.common.error.ErrorHandler;
import ru.surf.course.movierecommendations.util.Utilities;

import static ru.surf.course.movierecommendations.ui.screen.personCredits.PersonCreditsFragmentView.KEY_PERSON;
import static ru.surf.course.movierecommendations.ui.screen.personCredits.PersonCreditsFragmentView.KEY_PERSON_ID;

@PerScreen
public class PersonCreditsFragmentPresenter extends BasePresenter<PersonCreditsFragmentView> {

    final static int DATA_TO_LOAD = 1;
    private int dataLoaded = 0;

    private Retrofit retrofit;

    private Person currentPerson;

    @Inject
    public PersonCreditsFragmentPresenter(ErrorHandler errorHandler, Retrofit retrofit) {
        super(errorHandler);
        this.retrofit = retrofit;
    }

    @Override
    public void onLoadFinished() {
        super.onLoadFinished();

        dataLoaded = 0;
        if (getView().getArguments().containsKey(KEY_PERSON)) {
            currentPerson = (Person) getView().getArguments().getSerializable(KEY_PERSON);
        } else if (getView().getArguments().containsKey(KEY_PERSON_ID)) {
            currentPerson = new Person(getView().getArguments().getInt(KEY_PERSON_ID));
        }
        loadCredits(currentPerson);
    }

    private void loadCredits(final Person person) {
        GetCreditsTask getCreditsTask = new GetCreditsTask();
        getCreditsTask.addListener(result -> {
            person.setCredits(result);
            dataLoadComplete();
        });
        getCreditsTask.getPersonCredits(currentPerson.getId(), Utilities.getSystemLanguage());
    }

    private void dataLoadComplete() {
        dataLoaded++;
        Logger.d("data loaded:" + dataLoaded);
        if (dataLoaded == DATA_TO_LOAD) {
            getView().fillInformation(currentPerson);
            getView().hideProgressBar();
        }

    }
}
