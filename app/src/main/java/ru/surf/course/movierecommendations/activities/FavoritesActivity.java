package ru.surf.course.movierecommendations.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ru.surf.course.movierecommendations.DBHelper;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.adapters.FavoritesAdapter;
import ru.surf.course.movierecommendations.models.Favorite;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoriteRecyclerView;
    private TextView msg;
    private List<Favorite> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setupToolbar();
        init();
    }

    private void init() {
        msg = (TextView) findViewById(R.id.activity_favorite_msg);
        DBHelper dbHelper = DBHelper.getHelper(this);
        favoriteList = dbHelper.getAllFavorites();
        favoriteRecyclerView = (RecyclerView) findViewById(R.id.activity_favorite_list_rv);
        if (favoriteList != null && favoriteList.size() != 0) {
            FavoritesAdapter adapter = new FavoritesAdapter(this, favoriteList);
            favoriteRecyclerView.setAdapter(adapter);
            favoriteRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            favoriteRecyclerView.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_favorite_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favorites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
