package com.typingsolutions.passwordmanager.callbacks.click;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.activities.BackupRestoreActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;

public class ExpandCallback extends BaseCallback {

  private BackupRestoreActivity backupRestoreActivity;
  private boolean expanded;

  public ExpandCallback(Context context, BackupRestoreActivity backupRestoreActivity) {
    super(context);
    this.backupRestoreActivity = backupRestoreActivity;
    expanded = false;
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
    v.animate()
        .rotation(0)
        .rotationBy(180)
        .setDuration(150)
        .setInterpolator(new DecelerateInterpolator())
        .setListener(new ExpandAnimationListenerImplementation(v))
        .start();

    TextView hint = backupRestoreActivity.getHint();
    TextInputLayout passwordWrapper = backupRestoreActivity.getPasswordWrapper();
    TextInputLayout repeatPasswordWrapper = backupRestoreActivity.getRepeatPasswordWrapper();
    if (expanded) {
      /*hint.animate().alpha(1.f)
          .alphaBy(0.f)
          .setInterpolator(new DecelerateInterpolator())
          .setDuration(150)
          .start();*/

      hint.setVisibility(View.GONE);
      passwordWrapper.setVisibility(View.GONE);
      repeatPasswordWrapper.setVisibility(View.GONE);
    } else {
      hint.setVisibility(View.VISIBLE);
      passwordWrapper.setVisibility(View.VISIBLE);
      repeatPasswordWrapper.setVisibility(View.VISIBLE);
    }
    expanded = !expanded;
  }

  private class ExpandAnimationListenerImplementation implements Animator.AnimatorListener {
    private View view;

    public ExpandAnimationListenerImplementation(View view) {
      this.view = view;
    }

    @Override
    public void onAnimationStart(Animator animation) {
      view.setClickable(false);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
      view.setClickable(true);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
  }
}
