package com.typingsolutions.passwordmanager.callbacks;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;
import core.adapter.IItemTouchHelperAdapter;
import core.adapter.viewholder.PasswordOverviewViewHolder;
import core.data.PasswordProvider;

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
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    return false;
  }

  @Override
  public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
    AlertDialog dialog = new AlertDialog.Builder(context)
        .setMessage("Delete this password?")
        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
          }
        })
        .setNegativeButton("DISCARD", null)
        .create();
    dialog.show();
  }

  @Override
  public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);
  }
}
