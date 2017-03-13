package com.typingsolutions.kore.common;

import android.animation.Animator;
import android.view.View;

public class SetGoneOnEndAnimationListener implements Animator.AnimatorListener {
  private View mView;

  public SetGoneOnEndAnimationListener(View view) {
    mView = view;
  }

  @Override
  public void onAnimationStart(Animator animation) {

  }

  @Override
  public void onAnimationEnd(Animator animation) {
    if(mView.getVisibility() == View.VISIBLE) {
      mView.setVisibility(View.GONE);
    }
  }

  @Override
  public void onAnimationCancel(Animator animation) {

  }

  @Override
  public void onAnimationRepeat(Animator animation) {

  }
}
