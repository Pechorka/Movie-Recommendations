package ru.surf.course.movierecommendations;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Sergey on 09.02.2017.
 */

public class NotSwipeableViewPager extends ViewPager {

    private boolean swipeEnabled;

    public NotSwipeableViewPager(Context context) {
        super(context);
        swipeEnabled = false;
    }

    public NotSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        swipeEnabled = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onTouchEvent(event);
    }
}
