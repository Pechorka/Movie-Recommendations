package ru.surf.course.movierecommendations.ui.screen.personCredits;

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

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.presenter.MvpPresenter;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.ui.base.fragment.BaseFragmentView;
import ru.surf.course.movierecommendations.ui.screen.personCredits.adapters.PersonCreditsListAdapter;
import ru.surf.course.movierecommendations.util.Utilities;
import ru.surf.course.movierecommendations.ui.screen.movie.MovieActivityView;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.domain.movie.MovieInfo;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.domain.tvShow.TVShowInfo;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.GetCreditsTask;


public class PersonCreditsFragmentView extends BaseFragmentView {

    final static String KEY_PERSON = "person";
    final static String KEY_PERSON_ID = "person_id";

    @Inject
    PersonCreditsFragmentPresenter presenter;

    private ProgressBar progressBar;
    private RecyclerView mCreditsList;
    private PersonCreditsListAdapter mPersonCreditsListAdapter;


    public static PersonCreditsFragmentView newInstance(Person person) {
        PersonCreditsFragmentView personCreditsFragmentView = new PersonCreditsFragmentView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PERSON, person);
        personCreditsFragmentView.setArguments(bundle);
        return personCreditsFragmentView;
    }

    public static PersonCreditsFragmentView newInstance(int personId) {
        PersonCreditsFragmentView personCreditsFragmentView = new PersonCreditsFragmentView();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERSON_ID, personId);
        personCreditsFragmentView.setArguments(bundle);
        return personCreditsFragmentView;
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
        return DaggerPersonCreditsFragmentComponent.builder()
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

    void fillInformation(Person data) {
        mPersonCreditsListAdapter = new PersonCreditsListAdapter(getActivity(),
                data.getCredits());
        mPersonCreditsListAdapter.setListener(position -> {

            Media media = mPersonCreditsListAdapter.getCredits().get(position).getMedia();
            if (media instanceof MovieInfo) {
                MovieActivityView.start(getActivity(), (MovieInfo) media);
            } else if (media instanceof TVShowInfo) {
                return;
            }

        });
        mCreditsList.setAdapter(mPersonCreditsListAdapter);
    }

    void hideProgressBar() {
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
