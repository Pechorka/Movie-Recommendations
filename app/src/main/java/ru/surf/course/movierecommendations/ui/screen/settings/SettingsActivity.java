package ru.surf.course.movierecommendations.ui.screen.settings;

import static ru.surf.course.movierecommendations.ui.screen.splash.SplachActivity.KEY_IS_SETUP;
import static ru.surf.course.movierecommendations.ui.screen.splash.SplachActivity.KEY_RECOMMENDATIONS_SETUP;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.interactor.DBHelper;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.Filters;
import ru.surf.course.movierecommendations.ui.screen.recommendationsSetup.RecommendationsSetupActivityView;


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
      Preference reset = findPreference("reset");
      if (reset != null) {
        reset.setOnPreferenceClickListener(preference -> {
          buildConfirmatoryDialog().show();
          return true;
        });
      }
    }

    private Dialog buildConfirmatoryDialog() {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.reset_recommendations_confirmatory_dialog_title)
          .setPositiveButton(android.R.string.yes,
              (dialog, which) -> {
                resetRecommendations();
                buildStartSetupRecomDialog().show();
              })
          .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
      return builder.create();
    }

    private Dialog buildStartSetupRecomDialog() {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.start_setup_recommendations_dialog_title)
          .setPositiveButton(android.R.string.yes,
              (dialog, which) -> startRecommendationsSetupProcess())
          .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
      return builder.create();
    }

    private void resetRecommendations() {
      DBHelper helper = DBHelper.getHelper(getActivity());
      helper.deleteAllRecommendedGenres();
      DBHelper.recommendationsReset = true;
      SharedPreferences prefs = getActivity()
          .getSharedPreferences(KEY_RECOMMENDATIONS_SETUP, MODE_PRIVATE);
      prefs.edit().putBoolean(KEY_IS_SETUP, false).apply();
    }

    private void startRecommendationsSetupProcess() {
      RecommendationsSetupActivityView
          .startRecommendationsActivityWithClearBackstack(getActivity());
    }
  }
}
