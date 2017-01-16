package ru.surf.course.movierecommendations.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 13.01.2017.
 */

public class YearsRangeBar extends RelativeLayout {

    private TextView min;
    private TextView max;
    private RangeBar rangeBar;

    private int minYear;
    private int maxYear;

    public YearsRangeBar(Context context) {
        super(context);
        init(context);
        setup();
    }

    public YearsRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setup();
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
        min.setText(String.valueOf(minYear));
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
        max.setText(String.valueOf(maxYear));
    }

    public int getMinYear() {
        return minYear;
    }

    public int getMaxYear() {
        return maxYear;
    }


    private void init(Context context) {
        View rootView = inflate(context, R.layout.years_range_bar, this);
        min = (TextView) rootView.findViewById(R.id.rb_min_year);
        max = (TextView) rootView.findViewById(R.id.rb_max_year);
        rangeBar = (RangeBar) rootView.findViewById(R.id.rb);
        maxYear = new GregorianCalendar().get(Calendar.YEAR);
        minYear = 1930;
    }

    private void setup() {
        min.setText(String.valueOf(minYear));
        max.setText(String.valueOf(maxYear));
        rangeBar.setTickEnd(maxYear);
        rangeBar.setTickStart(minYear);
    }
}
