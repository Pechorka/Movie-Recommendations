package ru.surf.course.movierecommendations.ui.screen.favorites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.agna.ferro.mvp.component.ScreenComponent;
import java.util.List;
import javax.inject.Inject;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.Favorite;
import ru.surf.course.movierecommendations.ui.base.activity.BaseActivityView;
import ru.surf.course.movierecommendations.ui.base.activity.BasePresenter;
import ru.surf.course.movierecommendations.ui.screen.favorites.adapters.FavoritesAdapter;
import ru.surf.course.movierecommendations.ui.screen.movie.MovieActivityView;
import ru.surf.course.movierecommendations.ui.screen.tvShow.TvShowActivityView;

public class FavoritesActivityView extends BaseActivityView {

    @Inject
    FavoritesActivityPresenter presenter;

    private RecyclerView favoriteRecyclerView;
    private FavoritesAdapter adapter;
    private TextView msg;

    public static void start(Context context) {
        Intent intent = new Intent(context, FavoritesActivityView.class);
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
        return R.layout.activity_favorites;
    }

    @Override
    protected ScreenComponent createScreenComponent() {
        return DaggerFavoritesActivityComponent.builder()
                .activityModule(getActivityModule())
                .appComponent(getAppComponent())
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean viewRecreated) {
        super.onCreate(savedInstanceState, viewRecreated);

        setupToolbar();
        init();
    }

    private void init() {
        msg = (TextView) findViewById(R.id.activity_favorite_msg);
        favoriteRecyclerView = (RecyclerView) findViewById(R.id.activity_favorite_list_rv);
    }

    private void initFavoritesList(List<Favorite> favorites, DBHelper helper) {
        adapter = new FavoritesAdapter(this, favorites, helper);
        adapter.setListener((pos, v) -> activityToSwitch(adapter.getFavoriteList().get(pos)));
        favoriteRecyclerView.setAdapter(adapter);
        favoriteRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(favoriteRecyclerView);
    }

    public void setFavoritesContent(List<Favorite> favorites, DBHelper helper) {
        if (adapter == null) {
            initFavoritesList(favorites, helper);
        } else {
            adapter.setFavoriteList(favorites);
            adapter.notifyDataSetChanged();
        }
    }

    public void showEmptyMessage() {
        favoriteRecyclerView.setVisibility(View.GONE);
        msg.setVisibility(View.VISIBLE);
    }

    private void activityToSwitch(Favorite favorite) {
        switch (favorite.getMediaType()) {
            case movie:
                MovieActivityView.start(this, favorite.getMediaId());
                break;
            case tv:
                TvShowActivityView.start(this, favorite.getMediaId());
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_favorite_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favorites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
