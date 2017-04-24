package ru.surf.course.movierecommendations.ui.screen.favorites;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import ru.surf.course.movierecommendations.ui.screen.favorites.adapters.FavoritesAdapter;

/**
 * Created by sergey on 24.04.17.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

  private FavoritesAdapter adapter;

  public SimpleItemTouchHelperCallback(FavoritesAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
    return false;
  }

  @Override
  public void onSwiped(ViewHolder viewHolder, int direction) {
    adapter.onItemDismiss(viewHolder.getAdapterPosition());
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return true;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return false;
  }
}
