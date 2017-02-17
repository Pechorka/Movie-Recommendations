package ru.surf.course.movierecommendations.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 13.02.2017.
 */

public class ChooseSortDialogFragment extends DialogFragment {

    private static final String SORT_PREFS = "sort_prefs";
    private static final String CHOSEN_SORT = "chosen_sort";
    private CharSequence[] sortNames = new CharSequence[]{"By popularity", "By average votes"};
    private List<ChooseSortDialogFragment.SavePressedListener> listeners = new ArrayList<>();
    private int chosen;
    private int previous;
    private List<String> sortTypes = Arrays.asList("popularity", "vote_average");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        chosen = getChosen(getActivity());
        builder.setTitle("Choose sort type")
                .setSingleChoiceItems(sortNames, chosen, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        previous = chosen;
                        chosen = which;
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (previous != chosen) {
                            for (ChooseSortDialogFragment.SavePressedListener listener :
                                    listeners
                                    ) {
                                listener.saved();
                            }
                            saveChosen(chosen, getActivity());
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

    private int getChosen(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SORT_PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(CHOSEN_SORT, 0);
    }

    private boolean saveChosen(int chosen, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SORT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CHOSEN_SORT, chosen);
        return editor.commit();
    }


    public void addListener(ChooseSortDialogFragment.SavePressedListener toAdd) {
        listeners.add(toAdd);
    }

    public String getChosenSort() {
        return sortTypes.get(chosen);
    }

    public interface SavePressedListener {
        void saved();
    }

}
