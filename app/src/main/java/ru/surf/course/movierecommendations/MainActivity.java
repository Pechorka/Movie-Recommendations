package ru.surf.course.movierecommendations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import ru.surf.course.movierecommendations.Adapters.GridCustomAdapter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //временно
        String[] mNames = {
                "Dr. Strange","Fantastic Beasts"
                ,"Mad Max","Dr. Strange","Fantastic Beasts"
                ,"Mad Max","Dr. Strange","Fantastic Beasts"
                ,"Mad Max","Dr. Strange","Fantastic Beasts"
                ,"Mad Max"
        };
        int[] mThumbIds = {
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

        GridView gridView = (GridView)findViewById(R.id.grid_movies_list);
        GridCustomAdapter adapter = new GridCustomAdapter(mThumbIds,mNames,this);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
