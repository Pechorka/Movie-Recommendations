package ru.surf.course.movierecommendations.ui.screen.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Filters;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceFragment preferenceFragment = new MyPreferenceFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, preferenceFragment).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment {


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            ListPreference listPreference = (ListPreference) findPreference("filter");
            if (listPreference.getValue() == null || !Filters.isFilter(listPreference.getValue())) {
                listPreference.setValue(Filters.popular.toString());
            }
        }
    }
}
