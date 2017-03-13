package com.typingsolutions.kore.common;

import android.animation.Animator;
import android.view.View;


public class SetVisibleOnStartAnimationListener implements Animator.AnimatorListener {
  private View mView;

  public SetVisibleOnStartAnimationListener(View view) {
    mView = view;
  }

  @Override
  public void onAnimationStart(Animator animation) {
    if(mView.getVisibility() != View.VISIBLE) {
      mView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onAnimationEnd(Animator animation) {

  }

  @Override
  public void onAnimationCancel(Animator animation) {

  }

  @Override
  public void onAnimationRepeat(Animator animation) {

  }
}
