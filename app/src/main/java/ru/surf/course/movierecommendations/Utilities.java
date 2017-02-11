package ru.surf.course.movierecommendations;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class Utilities {

    private static final String LOG_TAG = "Utilities";
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

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

    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

}
