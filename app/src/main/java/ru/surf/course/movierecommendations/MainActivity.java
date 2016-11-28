package ru.surf.course.movierecommendations;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import ru.surf.course.movierecommendations.Adapters.GridCustomAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String MOVIE_NAME = "movie name";
    public static final String IMAGE_ID = "image id";

    public static void start(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static void start(Context context, Class c, String name, int imageID) {
        Intent intent = new Intent(context, c);
        intent.putExtra(MOVIE_NAME, name);
        intent.putExtra(IMAGE_ID, imageID);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        GridView gridView = (GridView) findViewById(R.id.grid_movies_list);
        GridCustomAdapter adapter = new GridCustomAdapter(mThumbIds, mNames, this);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                start(MainActivity.this, MovieInfoActivity.class, mNames[position], mThumbIds[position]);
            }
        });
    }
}
