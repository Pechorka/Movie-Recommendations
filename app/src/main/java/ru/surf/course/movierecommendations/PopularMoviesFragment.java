package ru.surf.course.movierecommendations;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import ru.surf.course.movierecommendations.Adapters.GridCustomAdapter;


public class PopularMoviesFragment extends Fragment {

    public static PopularMoviesFragment newInstance(){
        PopularMoviesFragment popularMoviesFragment = new PopularMoviesFragment();
        return popularMoviesFragment;
    }

    public interface onItemClickListener{
        public void itemClicked(int position, long id);
    }

    public void addOnItemClickListener(onItemClickListener listener) {
        listeners.add(listener);
    }

    List<onItemClickListener> listeners = new ArrayList<>();

    GridView gridView;

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

        gridView = (GridView)root.findViewById(R.id.grid_movies_list);
        GridCustomAdapter adapter = new GridCustomAdapter(mThumbIds, mNames, getActivity());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                for (onItemClickListener listener : listeners)
                    listener.itemClicked(position, id);
            }
        });

        return root;
    }

}
