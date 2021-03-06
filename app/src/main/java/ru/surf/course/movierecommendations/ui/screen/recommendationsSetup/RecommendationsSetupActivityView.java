package ru.surf.course.movierecommendations.ui.screen.recommendationsSetup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.agna.ferro.mvp.component.ScreenComponent;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Media;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.screen.main.MainActivityView;
import ru.surf.course.movierecommendations.ui.screen.recommendationsSetup.adapters.RecommendationsSetupListAdapter;
import ru.surf.course.movierecommendations.ui.screen.splash.SplachActivity;

public class RecommendationsSetupActivityView extends BaseActivityView {

    @Inject
    RecommendationsSetupActivityPresenter presenter;

    private RecyclerView recommendSetupList;
    private RecommendationsSetupListAdapter adapter;
    private View errorPlaceholder;

    public static void startRecommendationsActivityWithClearBackstack(Context context) {
        SplachActivity.startWithClearBackStack(context, RecommendationsSetupActivityView.class);
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
        return R.layout.activity_recommendations_setup;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerRecommendationsSetupActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);

        init();
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recommended_setup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recommendations_setup_menu_save:
                presenter.onSaveBtnClick();
                break;
            case R.id.recommendations_setup_menu_skip:
                setIsSetup(true);
                startMainActivityWithClearBackstack();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        recommendSetupList = (RecyclerView) findViewById(R.id.recommendations_setup_rv);

    }

    private void initMediaList(List<Media> medias) {
        adapter = new RecommendationsSetupListAdapter(this, medias);
        recommendSetupList.setAdapter(adapter);
        recommendSetupList.setLayoutManager(new GridLayoutManager(this, 2));
    }

    void setMediaListContent(List<Media> medias) {
        if (adapter == null)
            initMediaList(medias);
        else {
            adapter.setMediaList(medias);
            adapter.notifyDataSetChanged();
        }
    }

    Set<Integer> getCheckedGenres() {
        return adapter.getGenres();
    }

    void setIsSetup(boolean value) {
        SharedPreferences prefs = getSharedPreferences(SplachActivity.KEY_RECOMMENDATIONS_SETUP,
                MODE_PRIVATE);
        prefs.edit().putBoolean(SplachActivity.KEY_IS_SETUP, value).apply();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_recommendation_setup_toolbar);
        setSupportActionBar(toolbar);
    }

    void showProperMsg(boolean movie) {
        String title;
        if (movie) {
            title = getResources().getString(R.string.movie_setup_title);
        } else {
            title = getResources().getString(R.string.tvshow_setup_title);
        }
        makeSnackbar(title);
        getSupportActionBar().setTitle("");
    }

    private void makeSnackbar(String text) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_recommendation_setup);
        Snackbar.make(linearLayout, text, Snackbar.LENGTH_INDEFINITE).show();
    }

    public void showNoInternetMessage() {
        errorPlaceholder = findViewById(R.id.recommendations_setup_no_internet_screen);
        errorPlaceholder.setVisibility(View.VISIBLE);
        (findViewById(R.id.message_no_internet_button))
            .setOnClickListener(view -> presenter.onRetryClick());
    }

    public void hideNoInternetMessage() {
        errorPlaceholder.setVisibility(View.GONE);
    }

    void startMainActivity() {
        MainActivityView.start(this, MainActivityView.class);
    }

    void startMainActivityWithClearBackstack() {
        SplachActivity.startWithClearBackStack(this, MainActivityView.class);
    }

    public void startRecommendationsActivityWithClearBackstack() {
        SplachActivity.startWithClearBackStack(this, RecommendationsSetupActivityView.class);
    }
}
