package ru.surf.course.movierecommendations.custom_views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import ru.surf.course.movierecommendations.R;


public class FavoriteButton extends AppCompatButton {

  private boolean addedToFavorites = false;
  private FavoriteButtonListener listener;
  private Drawable icFavorite;
  private Drawable icFavoriteBorder;

  public FavoriteButton(Context context) {
    super(context);
    init(context);
  }

  public FavoriteButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public boolean isAddedToFavorites() {
    return addedToFavorites;
  }

  public void setInitialState(boolean addedToFavorites) {
    this.addedToFavorites = addedToFavorites;
    if (addedToFavorites) {
      this.setBackground(icFavorite);
    } else {
      this.setBackground(icFavoriteBorder);
    }
  }

  private void init(Context context) {
    icFavoriteBorder = ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white);
    icFavorite = ContextCompat.getDrawable(context, R.drawable.ic_favorite_white);
    this.setBackground(icFavoriteBorder);
    this.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        toggle();
      }
    });
  }

  public void toggle() {
    boolean result = true;
    if (addedToFavorites) {
      if (listener != null) {
        result = listener.removedFromFavorite();
      }
      if (result) {
        addedToFavorites = false;
        this.setBackground(icFavoriteBorder);
      }
    } else {
      if (listener != null) {
        result = listener.addedToFavorite();
      }
      if (result) {
        addedToFavorites = true;
        this.setBackground(icFavorite);
      }
    }
  }

  public void setListener(FavoriteButtonListener listener) {
    this.listener = listener;
  }

  public interface FavoriteButtonListener {

    boolean addedToFavorite();

    boolean removedFromFavorite();
  }
}
