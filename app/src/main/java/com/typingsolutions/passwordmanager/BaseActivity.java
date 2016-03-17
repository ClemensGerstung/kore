package com.typingsolutions.passwordmanager;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

  public static final long FAST_ANIMATION_DURATION = 250;
  private static boolean safeFlag;
  public static boolean debug = true;

  private List<BroadcastReceiver> mRegisteredReceiver;

  public BaseActivity() {
    this.mRegisteredReceiver = new ArrayList<>();
  }

  public synchronized static boolean isSafe() {
    return safeFlag;
  }

  protected synchronized static void setSafe(boolean safe) {
    BaseActivity.safeFlag = safe;
  }

  /**
   * Finds a {@see View} by it's id
   *
   * @param id  of {@see View} to find
   * @param <T> Type of {@see View} to find
   * @return the found {@see View}
   */
  protected <T extends View> T findCastedViewById(@IdRes int id) {
    return (T) this.findViewById(id);
  }

  /**
   * Starts an activity
   *
   * @param activity Type of activity to start
   */
  public void startActivity(Class<? extends Activity> activity) {
    this.startActivity(activity, false);
  }

  /**
   * Starts an activity
   *
   * @param activity Type of activity to start
   * @param finish   {@code true} finishes the current activity
   */
  public void startActivity(Class<? extends Activity> activity, boolean finish) {
    this.startActivity(activity, finish, 0, 0);
  }

  /**
   * Starts an activity
   *
   * @param activity  Type of activity to start
   * @param finish    {@code true} finishes the current activity
   * @param enterAnim A resource ID of the animation resource to use for the incoming activity. Use 0 for no animation.
   * @param exitAnim  A resource ID of the animation resource to use for the outgoing activity. Use 0 for no animation.
   */
  public void startActivity(Class<? extends Activity> activity, boolean finish, @AnimRes int enterAnim, @AnimRes int exitAnim) {
    Intent intent = new Intent(this, activity);
    this.startActivity(intent);

    this.overridePendingTransition(enterAnim, exitAnim);
    if (finish) ActivityCompat.finishAfterTransition(this);
  }

  /**
   * Registers a {@see BroadcastReceiver} which will be automatically be removed if the {@see BaseActivity} is destroyed
   *
   * @param receiver the {@see BroadcastReceiver} to listen
   * @param filter   to listen at
   * @return the newly created {@see Intent} returned from the base class
   */
  public Intent registerAutoRemoveReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    mRegisteredReceiver.add(receiver);
    return super.registerReceiver(receiver, filter);
  }

  /**
   * Will be called if the {@see Activity} will be destroyed (completely finished)
   */
  @Override
  protected void onDestroy() {
    super.onDestroy();

    for (BroadcastReceiver receiver : this.mRegisteredReceiver) {
      super.unregisterReceiver(receiver);
    }
  }

  /**
   * Sets flags so the user cannot take screenshots or screen recordings
   */
  protected void setSecurityFlags() {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
  }

  public boolean isServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  protected abstract View getSnackbarRelatedView();

  public void makeSnackbar(String message) {
    if (getSnackbarRelatedView() == null) return;

    Snackbar.make(getSnackbarRelatedView(), message, Snackbar.LENGTH_LONG).show();
  }

  public void showErrorLog(Class clazz, Exception e) {
    Log.e(clazz.getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
  }

  public synchronized void showView(@NonNull View view, @AnimRes int animation) {
    if (view == null)
      throw new IllegalArgumentException("Views Array cannot be null or empty");

    if (view.getVisibility() != View.VISIBLE) {
      view.clearAnimation();
      view.setVisibility(View.VISIBLE);
      Animation anim = android.view.animation.AnimationUtils.loadAnimation(this, animation);
      anim.setDuration(FAST_ANIMATION_DURATION);
      anim.setInterpolator(new DecelerateInterpolator());

      view.startAnimation(anim);
    }
  }

  public synchronized void hideView(@NonNull View view, @AnimRes int animation) {
    if (view == null) return;

    if (view.getVisibility() != View.VISIBLE) return;

    Animation anim = android.view.animation.AnimationUtils.loadAnimation(this, animation);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.setDuration(FAST_ANIMATION_DURATION);
    anim.setAnimationListener(new LocalAnimationListener(view));
    view.startAnimation(anim);
  }

  protected synchronized void startAnimator(@NonNull View view, @AnimatorRes int res) {
    Animator animator = AnimatorInflater.loadAnimator(this, res);
    animator.setTarget(view);
    animator.setDuration(FAST_ANIMATION_DURATION);
    animator.start();
  }

  private static class LocalAnimationListener implements Animation.AnimationListener {
    private View view;

    public LocalAnimationListener(View view) {
      this.view = view;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
      view.setVisibility(View.GONE);
    }
  }
}
