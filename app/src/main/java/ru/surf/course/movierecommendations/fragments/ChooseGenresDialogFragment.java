package ru.surf.course.movierecommendations.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 16.01.2017.
 */

public class ChooseGenresDialogFragment extends DialogFragment {

    private static final String KEY_GENRES = "genres";
    private List<Integer> selected;

    //    public static ChooseGenresDialogFragment newInstance(List<String> genres){
//        ChooseGenresDialogFragment fragment = new ChooseGenresDialogFragment();
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList(KEY_GENRES, (ArrayList<String>) genres);
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selected = new ArrayList<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Genres")
                .setMultiChoiceItems(R.array.genres, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selected.add(which);
                        } else if (selected.contains(which)) {
                            selected.remove(which);
                        }
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
