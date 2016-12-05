package ru.surf.course.movierecommendations;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.surf.course.movierecommendations.Adapters.GridMoviesAdapter;


public class PopularMoviesFragment extends Fragment {

    public static PopularMoviesFragment newInstance(){
        PopularMoviesFragment popularMoviesFragment = new PopularMoviesFragment();
        return popularMoviesFragment;
    }

    RecyclerView recyclerView;

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
        GridMoviesAdapter adapter = new GridMoviesAdapter(getActivity(), movieInfoList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        return root;
    }

}
