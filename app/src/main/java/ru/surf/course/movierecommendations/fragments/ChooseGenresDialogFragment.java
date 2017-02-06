package ru.surf.course.movierecommendations.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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


    private static final String KEY_GENRES = "genres";
    private Set<Integer> selected;
    private Set<Integer> uncertainSelected;
    private List<SavePressedListener> listeners = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selected = new HashSet<>();
        uncertainSelected = new HashSet<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Genres")
                .setMultiChoiceItems(R.array.genres, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            uncertainSelected.add(which);
                        } else if (uncertainSelected.contains(which)) {
                            uncertainSelected.remove(which);
                        }
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        selected.addAll(uncertainSelected);
                        uncertainSelected.clear();
                        for (SavePressedListener listener :
                                listeners
                                ) {
                            listener.saved();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        uncertainSelected.clear();
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Set<Integer> getSelected() {
        return selected;
    }

    public void addListener(SavePressedListener toAdd) {
        listeners.add(toAdd);
    }

    public interface SavePressedListener {
        void saved();
    }

}
