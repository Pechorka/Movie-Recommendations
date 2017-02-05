package ru.surf.course.movierecommendations;

import android.util.Log;

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

}
