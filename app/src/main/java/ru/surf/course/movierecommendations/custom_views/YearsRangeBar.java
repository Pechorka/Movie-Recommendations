package ru.surf.course.movierecommendations.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import java.util.Calendar;
import java.util.GregorianCalendar;
import ru.surf.course.movierecommendations.R;

/**
 * Created by Sergey on 12.01.2017.
 */

public class YearsRangeBar extends RelativeLayout {

  private TextView minY;
  private TextView maxY;
  private CrystalRangeSeekbar rangeBar;

  private int minYear;
  private int maxYear;
  private int curMinYear;
  private int curMaxYear;

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
    minY.setText(String.valueOf(minYear));
    if (curMinYear < minYear) {
      curMinYear = minYear;
    }
  }

  public void setMaxYear(int maxYear) {
    this.maxYear = maxYear;
    maxY.setText(String.valueOf(maxYear));
    if (curMaxYear > maxYear) {
      curMaxYear = maxYear;
    }
  }

  public int getCurMinYear() {
    return curMinYear;
  }

  public int getCurMaxYear() {
    return curMaxYear;
  }


  private void init(Context context) {
    View rootView = inflate(context, R.layout.years_range_bar, this);
    minY = (TextView) rootView.findViewById(R.id.rb_min_year);
    maxY = (TextView) rootView.findViewById(R.id.rb_max_year);
    rangeBar = (CrystalRangeSeekbar) rootView.findViewById(R.id.rb);
    maxYear = new GregorianCalendar().get(Calendar.YEAR);
    minYear = 1930;
    curMaxYear = maxYear;
    curMinYear = minYear;

  }

  private void setup() {
    minY.setText(String.valueOf(minYear));
    maxY.setText(String.valueOf(maxYear));
    rangeBar.setMaxValue(maxYear);
    rangeBar.setMinValue(minYear);
    rangeBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
      @Override
      public void valueChanged(Number minValue, Number maxValue) {
        minY.setText(String.valueOf(minValue));
        maxY.setText(String.valueOf(maxValue));
        if (curMinYear != minValue.intValue()) {
          curMinYear = minValue.intValue();
        }
        if (curMaxYear != maxValue.intValue()) {
          curMaxYear = maxValue.intValue();
        }
      }

    });

  }

  public void setOnRangeSeekbarFinalValueListener(OnRangeSeekbarFinalValueListener listener) {
    rangeBar.setOnRangeSeekbarFinalValueListener(listener);
  }

  public void setOnRangeChangeListener(OnRangeSeekbarChangeListener listener) {
    rangeBar.setOnRangeSeekbarChangeListener(listener);
  }
}
