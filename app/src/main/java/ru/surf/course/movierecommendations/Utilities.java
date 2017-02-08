package ru.surf.course.movierecommendations;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import java.lang.reflect.Field;

public class Utilities {

    private static final String LOG_TAG = "Utilities";

    public static boolean checkString(String string) {
        return !(string == null || string.equals("") || string.equals("null"));
    }

    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

}
