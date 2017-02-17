package com.typingsolutions.kore.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;


public class BottomSheetViewerFragment extends BottomSheetDialogFragment {
  private int mLayout;
  private OnDismissListener mOnDismissListener;

  public static BottomSheetViewerFragment create(@LayoutRes int layout) {
    BottomSheetViewerFragment fragment = new BottomSheetViewerFragment();
    fragment.mLayout = layout;
    return fragment;
  }

  private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
      if (newState == BottomSheetBehavior.STATE_HIDDEN) {
        dismiss();
        if(mOnDismissListener != null)
          mOnDismissListener.onDismiss(BottomSheetViewerFragment.this);
      }

    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

    }
  };

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("layout", mLayout);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {


    return super.onCreateDialog(savedInstanceState);
  }

  @Override
  public void setupDialog(Dialog dialog, int style) {
    super.setupDialog(dialog, style);

    View root = View.inflate(getContext(), mLayout, null);
    View parent = (View) root.getParent();

    if (!(root instanceof RecyclerView) && !(root instanceof ListView)) {
      ScrollView.LayoutParams paramsScrollView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

      ScrollView scrollView = new ScrollView(getContext());
      scrollView.setLayoutParams(paramsScrollView);
      scrollView.addView(root);
      dialog.setContentView(scrollView);

      parent = (View) scrollView.getParent();
    }

    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
    CoordinatorLayout.Behavior behavior = params.getBehavior();

    if (behavior != null && behavior instanceof BottomSheetBehavior) {
      ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
    }
  }

  public void setOnDismissListener(OnDismissListener onDismissListener) {
    mOnDismissListener = onDismissListener;
  }

  public static interface OnDismissListener {
    void onDismiss(BottomSheetViewerFragment fragment);
  }
}
