package com.typingsolutions.passwordmanager;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

  protected BaseActivity activity;

  public BaseViewHolder(BaseActivity activity, View itemView) {
    super(itemView);
    this.activity = activity;
  }

  /**
   * Called when the {@link ItemTouchHelper} first registers an item as being moved or swiped.
   * Implementations should update the item view to indicate it's active state.
   */
  public void onItemSelected() {
    // nothing to do
    // child class will override
  }

  /**
   * Called when the {@link ItemTouchHelper} has completed the move or swipe, and the active item
   * state should be cleared.
   */
  public void onItemClear() {
    // nothing to do
    // child class will override
  }
}
