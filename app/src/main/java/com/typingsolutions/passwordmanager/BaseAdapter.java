package com.typingsolutions.passwordmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

public abstract class BaseAdapter<T extends BaseViewHolder> extends RecyclerView.Adapter<T> {
  protected BaseActivity activity;
  protected LayoutInflater inflater;

  public BaseAdapter(BaseActivity activity) {
    this.activity = activity;
    this.inflater = LayoutInflater.from(activity);
  }

  /**
   * Called when an item has been dragged far enough to trigger a move. This is called every time
   * an item is shifted, and not at the end of a "drop" event.
   *
   * @param fromPosition The start position of the moved item.
   * @param toPosition   Then end position of the moved item.
   * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
   * @see RecyclerView.ViewHolder#getAdapterPosition()
   */
  public void onItemMove(int fromPosition, int toPosition) {
    // nothing to do
    // child class will override
  }

  /**
   * Called when an item has been dismissed by a swipe.
   *
   * @param position The position of the item dismissed.
   * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
   * @see RecyclerView.ViewHolder#getAdapterPosition()
   */
  public void onItemDismiss(int position) {
    // nothing to do
    // child class will override
  }
}
