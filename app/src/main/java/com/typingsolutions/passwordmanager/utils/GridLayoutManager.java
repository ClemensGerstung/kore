package com.typingsolutions.passwordmanager.utils;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class GridLayoutManager extends android.support.v7.widget.GridLayoutManager {
  public GridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public GridLayoutManager(Context context, int spanCount) {
    super(context, spanCount);
  }

  public GridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
    super(context, spanCount, orientation, reverseLayout);
  }

  @Override
  public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    if(recycler.getScrapList().size() == 0) return;
    super.onLayoutChildren(recycler, state);
  }
}
