package ru.surf.course.movierecommendations;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.surf.course.movierecommendations.Adapters.GridMoviesAdapter;
import ru.surf.course.movierecommendations.Adapters.ListMoviesAdapter;


public class PopularMoviesFragment extends Fragment {

    public static PopularMoviesFragment newInstance(){
        PopularMoviesFragment popularMoviesFragment = new PopularMoviesFragment();
        return popularMoviesFragment;
    }

    private RecyclerView recyclerView;
    GridMoviesAdapter gridMoviesAdapter;
    ListMoviesAdapter listMoviesAdapter;
    LinearLayoutManager linearLayoutManager;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private boolean grid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_popular_movies, container, false);


        //временно
        final String[] mNames = {
                "Dr. Strange", "Fantastic Beasts"
                , "Mad Max", "Dr. Strange", "Fantastic Beasts"
                , "Mad Max", "Dr. Strange", "Fantastic Beasts"
                , "Mad Max", "Dr. Strange", "Fantastic Beasts"
                , "Mad Max"
        };
        final int[] mThumbIds = {
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max,
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max,
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max,
                R.drawable.dr_strange,
                R.drawable.fantastic_beasts,
                R.drawable.mad_max
        };

        recyclerView = (RecyclerView) root.findViewById(R.id.fragment_popular_rv);
        List<MovieInfo> movieInfoList = MovieInfo.createMovieInfoList(mThumbIds, mNames);

        gridMoviesAdapter = new GridMoviesAdapter(getActivity(), movieInfoList);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        listMoviesAdapter = new ListMoviesAdapter(getActivity(), movieInfoList);
        recyclerView.setAdapter(gridMoviesAdapter);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        grid = true;

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_switch_layout) {
            if (grid) {
                recyclerView.setAdapter(listMoviesAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_grid_on_black_48dp, null));
                grid = false;
            } else {
                recyclerView.setAdapter(gridMoviesAdapter);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_black_48dp, null));
                grid = true;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

