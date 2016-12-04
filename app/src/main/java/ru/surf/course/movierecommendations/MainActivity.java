package ru.surf.course.movierecommendations;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import ru.surf.course.movierecommendations.Adapters.GridCustomAdapter;

public class MainActivity extends AppCompatActivity implements PopularMoviesFragment.onItemClickListener {


    public static void start(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_main_toolbar);

        PopularMoviesFragment popularMoviesFragment = PopularMoviesFragment.newInstance();
        popularMoviesFragment.addOnItemClickListener(this);

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.activity_main_container, popularMoviesFragment).commit();



    }

    @Override
    public void itemClicked(int position, long id) {
        Toast.makeText(this, "shit", Toast.LENGTH_LONG).show();
        MovieInfoFragment movieInfoFragment = MovieInfoFragment.newInstance(346672);
        getFragmentManager().beginTransaction().replace(R.id.activity_main_container, movieInfoFragment).addToBackStack("movieInfo").commit();
    }
}
