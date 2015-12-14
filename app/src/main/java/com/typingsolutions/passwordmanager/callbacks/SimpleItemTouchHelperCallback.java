package com.typingsolutions.passwordmanager.callbacks;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.typingsolutions.passwordmanager.adapter.IItemTouchHelperAdapter;
import com.typingsolutions.passwordmanager.adapter.PasswordOverviewAdapter;
import com.typingsolutions.passwordmanager.adapter.viewholder.IItemTouchHelperViewHolder;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
  private Context context;
  private IItemTouchHelperAdapter adapter;

  public SimpleItemTouchHelperCallback(Context context, IItemTouchHelperAdapter adapter) {
    this.context = context;
    this.adapter = adapter;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return true;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
    adapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override
  public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
    adapter.onItemDismiss(viewHolder.getAdapterPosition());
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    IItemTouchHelperViewHolder itemViewHolder = (IItemTouchHelperViewHolder) viewHolder;
    itemViewHolder.onItemClear();
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      IItemTouchHelperViewHolder itemViewHolder = (IItemTouchHelperViewHolder) viewHolder;
      itemViewHolder.onItemSelected();
    }

    super.onSelectedChanged(viewHolder, actionState);
  }
}
