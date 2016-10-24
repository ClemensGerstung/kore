package com.typingsolutions.passwordmanager.fragments;

import android.app.Dialog;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

public class BottomSheetViewerFragment extends BottomSheetDialogFragment {

  private int mLayout;

  public static BottomSheetDialogFragment create(@LayoutRes int layout) {
    BottomSheetViewerFragment fragment = new BottomSheetViewerFragment();
    fragment.mLayout = layout;
    return fragment;
  }

  private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
      if (newState == BottomSheetBehavior.STATE_HIDDEN) {
        dismiss();
      }

    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

    }
  };

  @Override
  public void setupDialog(Dialog dialog, int style) {
    super.setupDialog(dialog, style);
    View root = View.inflate(getContext(), mLayout, null);

    ScrollView.LayoutParams paramsScrollView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

    ScrollView scrollView = new ScrollView(getContext());
    scrollView.setLayoutParams(paramsScrollView);
    scrollView.addView(root);
    dialog.setContentView(scrollView);

    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) root.getParent().getParent()).getLayoutParams();
    CoordinatorLayout.Behavior behavior = params.getBehavior();

    if( behavior != null && behavior instanceof BottomSheetBehavior ) {
      ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
    }
  }
}
