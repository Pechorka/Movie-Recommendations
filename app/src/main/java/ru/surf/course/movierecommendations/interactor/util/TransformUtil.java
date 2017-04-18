package ru.surf.course.movierecommendations.interactor.util;

import android.support.annotation.Nullable;
import android.text.Html;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.ArrayList;
import java.util.Collection;

import ru.surf.course.movierecommendations.interactor.common.network.AnnotationExclusionStrategy;
import ru.surf.course.movierecommendations.util.SdkUtil;


public class TransformUtil {

    public static final Gson gson = new GsonBuilder()
            .setExclusionStrategies(new AnnotationExclusionStrategy())
            .create();


    /**
     * Заменяет форматирующие символы Html на нормальные (например &quot; -> ")
     */
    public static String sanitizeHtmlString(String string) {
        if (string == null) {
            return null;
        }
        if (SdkUtil.supportsVersionCode(android.os.Build.VERSION_CODES.N)) {
            return Html.fromHtml(string, 0).toString();
        } else {
            return Html.fromHtml(string).toString();
        }
    }

}
