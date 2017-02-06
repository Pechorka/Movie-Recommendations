package ru.surf.course.movierecommendations.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 16.01.2017.
 */

public class ChooseGenresDialogFragment extends DialogFragment {

    public static final String CHECKED_GENRES = "checked_genres";
    public static final String CHECKED_ARRAY = "checked_array";
    private List<SavePressedListener> listeners = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final boolean[] checked = loadChecked(getActivity());
        builder.setTitle("Choose Genres")
                .setMultiChoiceItems(R.array.genres, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveChecked(checked, getActivity());
                        for (SavePressedListener listener :
                                listeners
                                ) {
                            listener.saved();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    private boolean saveChecked(boolean[] array, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(CHECKED_GENRES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CHECKED_ARRAY + "_size", array.length);

        for (int i = 0; i < array.length; i++)
            editor.putBoolean(CHECKED_ARRAY + "_" + i, array[i]);

        return editor.commit();
    }


    private boolean[] loadChecked(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(CHECKED_GENRES, Context.MODE_PRIVATE);
        int size = prefs.getInt(CHECKED_ARRAY + "_size", 0);
        boolean[] array = new boolean[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getBoolean(CHECKED_ARRAY + "_" + i, false);
        return array;
    }

    public Set<Integer> getSelected(Context context) {

        boolean[] checked = loadChecked(context);
        Set<Integer> selected = new HashSet<>();
        for (int i = 0; i < checked.length; i++) {
            if (checked[i]) {
                selected.add(i);
            }
        }
        return selected;
    }

    public void addListener(SavePressedListener toAdd) {
        listeners.add(toAdd);
    }

    public interface SavePressedListener {
        void saved();
    }

}
