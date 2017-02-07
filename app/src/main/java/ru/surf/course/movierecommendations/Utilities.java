package ru.surf.course.movierecommendations;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import java.lang.reflect.Field;

public class Utilities {

    private static final String LOG_TAG = "Utilities";

    public static void copyFields(Object from, Object to) {
        Field[] fields = from.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Field fieldFrom = from.getClass().getDeclaredField(field.getName());
                Object value = fieldFrom.get(from);
                to.getClass().getDeclaredField(field.getName()).set(to, value);
            } catch (IllegalAccessException e) {
                Log.d(LOG_TAG, "Copy error" + e.getMessage());
            } catch (NoSuchFieldException e) {
                Log.d(LOG_TAG, "Copy error" + e.getMessage());
            }
        }
    }

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
