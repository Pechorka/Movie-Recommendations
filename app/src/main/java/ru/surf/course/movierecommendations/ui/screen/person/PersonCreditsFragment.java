package ru.surf.course.movierecommendations.ui.screen.person;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.ui.screen.person.adapters.PersonCreditsListAdapter;
import ru.surf.course.movierecommendations.util.Utilities;
import ru.surf.course.movierecommendations.ui.screen.movie.MovieActivity;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetCreditsTask;

/**
 * Created by andrew on 2/9/17.
 */

public class PersonCreditsFragment extends Fragment {

  final static String KEY_PERSON = "person";
  final static String KEY_PERSON_ID = "person_id";
  final static int DATA_TO_LOAD = 1;
  final String LOG_TAG = getClass().getSimpleName();

  private ProgressBar progressBar;
  private Person currentPerson;
  private RecyclerView mCreditsList;
  private PersonCreditsListAdapter mPersonCreditsListAdapter;

  private int dataLoaded = 0;

  public static PersonCreditsFragment newInstance(Person person) {
    PersonCreditsFragment personCreditsFragment = new PersonCreditsFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(KEY_PERSON, person);
    personCreditsFragment.setArguments(bundle);
    return personCreditsFragment;
  }

  public static PersonCreditsFragment newInstance(int personId) {
    PersonCreditsFragment personCreditsFragment = new PersonCreditsFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_PERSON_ID, personId);
    personCreditsFragment.setArguments(bundle);
    return personCreditsFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (getArguments() == null) {
      onDestroy();
    }

    View root = inflater.inflate(R.layout.fragment_person_credits, container, false);
    initViews(root);
    setupViews(root);

    return root;
  }

  private void initViews(View root) {
    progressBar = (ProgressBar) root.findViewById(R.id.person_credits_progress_bar);
    if (progressBar != null) {
      progressBar.setIndeterminate(true);
      progressBar.getIndeterminateDrawable()
          .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent),
              PorterDuff.Mode.MULTIPLY);
    }

    mCreditsList = (RecyclerView) root.findViewById(R.id.person_credits_credits_list);
  }

  private void setupViews(View root) {
    mCreditsList.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    dataLoaded = 0;
    if (getArguments().containsKey(KEY_PERSON)) {
      currentPerson = (Person) getArguments().getSerializable(KEY_PERSON);
    } else if (getArguments().containsKey(KEY_PERSON_ID)) {
      currentPerson = new Person(getArguments().getInt(KEY_PERSON_ID));
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

  private void fillInformation() {
    mPersonCreditsListAdapter = new PersonCreditsListAdapter(getActivity(),
        currentPerson.getCredits());
    mPersonCreditsListAdapter.setListener(position -> {

      Media media = mPersonCreditsListAdapter.getCredits().get(position).getMedia();
      if (media instanceof MovieInfo) {
        MovieActivity.start(getActivity(), (MovieInfo) media);
      } else if (media instanceof TVShowInfo) {
        return;
      }

    });
    mCreditsList.setAdapter(mPersonCreditsListAdapter);
  }

  private void dataLoadComplete() {
    dataLoaded++;
    Log.v(LOG_TAG, "data loaded:" + dataLoaded);
    if (dataLoaded == DATA_TO_LOAD) {
      fillInformation();
      View progressBarPlaceholder = null;
      if (getView() != null) {
        progressBarPlaceholder = getView()
            .findViewById(R.id.person_credits_progress_bar_placeholder);
      }
      if (progressBarPlaceholder != null) {
        progressBarPlaceholder.setVisibility(View.GONE);
      }

    }

  }
}
