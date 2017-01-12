package ru.surf.course.movierecommendations.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Sergey on 12.01.2017.
 */

public class CustomFilterOptions extends LinearLayout {

    private YearsRangeBar yearsRangeBar;

    public CustomFilterOptions(Context context) {
        super(context);
        init(context);
    }

    public CustomFilterOptions(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        yearsRangeBar = new YearsRangeBar(context);
    }

}
