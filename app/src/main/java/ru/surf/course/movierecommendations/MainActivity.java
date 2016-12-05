package ru.surf.course.movierecommendations;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


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

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.activity_main_container, popularMoviesFragment).commit();


    }

    public void switchContent(int id, Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(id, fragment).addToBackStack(null).commit();
    }

}
