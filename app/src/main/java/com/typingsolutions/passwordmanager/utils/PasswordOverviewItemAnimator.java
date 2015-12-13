package com.typingsolutions.passwordmanager.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;


public class PasswordOverviewItemAnimator extends RecyclerView.ItemAnimator {
  private Context context;
  List<RecyclerView.ViewHolder> viewHolders = new ArrayList<>();

  public PasswordOverviewItemAnimator(Context context) {
    this.context = context;
  }


  @Override
  public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
    return false;
  }

  @Override
  public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
    viewHolder.itemView.setVisibility(View.GONE);
    return viewHolders.add(viewHolder);
  }

  @Override
  public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
    return false;
  }

  @Override
  public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
    return false;
  }

  @Override
  public void runPendingAnimations() {
    if (viewHolders.isEmpty())
      return;

    for (final RecyclerView.ViewHolder viewHolder : viewHolders) {
      ViewUtils.show(context, viewHolder.itemView, android.support.design.R.anim.design_fab_in);
    }
  }

  @Override
  public void endAnimation(RecyclerView.ViewHolder item) {

  }

  @Override
  public void endAnimations() {

  }

  @Override
  public boolean isRunning() {
    return !viewHolders.isEmpty();
  }
}
