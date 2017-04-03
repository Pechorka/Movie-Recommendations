package ru.surf.course.movierecommendations.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 19.02.2017.
 */

public class SettingsActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new MyPreferenceFragment()).commit();
  }

  public static class MyPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preferences);
    }
  }
}
