package ru.surf.course.movierecommendations.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import ru.surf.course.movierecommendations.R;

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
        View rootView = inflate(context, R.layout.custom_filter_options, this);
        yearsRangeBar = (YearsRangeBar) rootView.findViewById(R.id.years_rb);
    }

}
