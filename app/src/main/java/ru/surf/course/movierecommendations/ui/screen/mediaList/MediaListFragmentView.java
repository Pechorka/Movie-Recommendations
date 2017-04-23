package ru.surf.course.movierecommendations.ui.screen.mediaList;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agna.ferro.mvp.component.ScreenComponent;
import com.agna.ferro.mvp.presenter.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Tasks;
import ru.surf.course.movierecommendations.ui.base.fragment.BaseFragmentView;
import ru.surf.course.movierecommendations.ui.screen.customFilter.CustomFilterActivityView;
import ru.surf.course.movierecommendations.ui.screen.mediaList.adapters.GridMediaAdapter;
import ru.surf.course.movierecommendations.ui.screen.mediaList.listeners.EndlessRecyclerViewScrollListener;

import static android.app.Activity.RESULT_OK;
import static ru.surf.course.movierecommendations.ui.screen.main.MainActivityPresenter.KEY_MEDIA;


public class MediaListFragmentView extends BaseFragmentView {

    private static final int GET_GENRES_REQUEST = 1;
    public final static String KEY_MAX_YEAR = "maxYear";
    public final static String KEY_MIN_YEAR = "minYear";
    public final static String KEY_GENRES = "genreIds";
    public final static String KEY_SORT_TYPE = "sort_type";
    public final static String KEY_SORT_DIRECTION = "sort_direction";
    final static String KEY_QUERY = "query";
    final static String KEY_REGION = "region";
    final static String KEY_TASK = "task";
    final static String KEY_MEDIA_ID = "id";

    @Inject
    MediaListFragmentPresenter presenter;


    private RecyclerView recyclerView;
    private GridMediaAdapter gridMediaAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private FloatingActionButton showCustomFilterOpt;
    private View progressBarPlaceholder;


    public static MediaListFragmentView newInstance(String query, String region,
                                                    Tasks task, Media.MediaType mediaType) {
        MediaListFragmentView mediaListFragmentView = new MediaListFragmentView();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_QUERY, query);
        bundle.putSerializable(KEY_TASK, task);
        bundle.putSerializable(KEY_MEDIA, mediaType);
        bundle.putString(KEY_REGION, region);
        mediaListFragmentView.setArguments(bundle);
        return mediaListFragmentView;
    }

    @Override
    public MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + this.toString();
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerMediaListFragmentComponent.builder()
                .fragmentModule(getFragmentModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_media_list, container, false);
        initViews(root);
        setupViews();

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GENRES_REQUEST) {
            if (resultCode == RESULT_OK) {
                presenter.onGetGenresResult(data);
            }
        }
    }

    private void initViews(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.media_list_rv);
        if (getActivity().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        } else {
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        }
        showCustomFilterOpt = (FloatingActionButton) root.findViewById(R.id.media_list_fab);
    }

    private void setupViews() {
        gridMediaAdapter = new GridMediaAdapter(getActivity(), new ArrayList<>(1));
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(gridMediaAdapter);
        recyclerView
                .addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        presenter.onLoadMore();
                    }
                });
        showCustomFilterOpt.setOnClickListener(v -> presenter.onCustomFilterBtnClick());
    }

    void startCustomFilterActivity(String sort_type, String sort_direction, Media.MediaType mediaType, String maxYear, String minYear) {
        Intent intent = new Intent(getActivity(), CustomFilterActivityView.class);
        intent.putExtra(KEY_SORT_TYPE, sort_type);
        intent.putExtra(KEY_SORT_DIRECTION, sort_direction);
        intent.putExtra(KEY_MEDIA, mediaType);
        intent.putExtra(KEY_MAX_YEAR, Integer.parseInt(maxYear));
        intent.putExtra(KEY_MIN_YEAR, Integer.parseInt(minYear));
        startActivityForResult(intent, GET_GENRES_REQUEST);
    }


    public void setCallOptionsVisibility(int visibility) {
        showCustomFilterOpt.setVisibility(visibility);
    }

    public void showPlaceholder() {
        if (getView() != null) {
            progressBarPlaceholder = getView().findViewById(R.id.movie_list_progress_bar_placeholder);
        }
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    public void hidePlaceholder() {
        if (getView() != null) {
            progressBarPlaceholder = getView().findViewById(R.id.movie_list_progress_bar_placeholder);
        }
        if (progressBarPlaceholder != null) {
            progressBarPlaceholder.setVisibility(View.GONE);
        }
    }

    public void fillInformation(List<Media> mediaList) {
        gridMediaAdapter.setMediaList(mediaList);
    }


}
