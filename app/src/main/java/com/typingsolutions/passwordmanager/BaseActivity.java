package com.typingsolutions.passwordmanager;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import com.typingsolutions.passwordmanager.services.LoginService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public abstract class BaseActivity extends AppCompatActivity {

  public static final long FAST_ANIMATION_DURATION = 250;
  public static boolean debug = true;
  private static LruCache<Integer, Bitmap> images;

  static {
    images = new LruCache<>(4 * 1024 * 1024);
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

  public void startActivity(Class<? extends Activity> activity, Bundle bundle) {
    startActivity(activity, bundle, false);
  }

  /**
   * Starts an activity
   *
   * @param activity Type of activity to start
   * @param finish   {@code true} finishes the current activity
   */
  public void startActivity(Class<? extends Activity> activity, boolean finish) {
    startActivity(activity, finish, 0, 0);
  }

  public void startActivity(Class<? extends Activity> activity, Bundle bundle, boolean finish) {
    Intent intent = new Intent(this, activity);
    intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
    intent.replaceExtras(bundle);

    if (finish) ActivityCompat.finishAfterTransition(this);
    startActivity(intent);
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
    intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
    overridePendingTransition(enterAnim, exitAnim);
    if (finish) ActivityCompat.finishAfterTransition(this);

    startActivity(intent);
  }

  /**
   * Sets flags so the user cannot take screenshots or screen recordings
   */
  protected void setSecurityFlags() {
    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
  }

  public void setMenuItemEnabled(Toolbar toolbar, int index, boolean enable) {
    try {
      MenuItem item = toolbar.getMenu().getItem(index);
      item.setEnabled(enable);
      item.getIcon().setAlpha(enable ? 255 : 64);
    } catch (Exception e) {
      showErrorLog(this.getClass(), e);
    }
  }

  protected boolean isServiceRunning(Class<?> serviceClass) {
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

  public static void showErrorLog(Class sender, Exception e) {
    Log.e(sender.getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
  }

  public synchronized void showViewAnimated(@NonNull View view, @AnimRes int animation) {
    if (view == null)
      throw new IllegalArgumentException("View cannot be null");

    if (view.getVisibility() != View.VISIBLE) {
      view.clearAnimation();
      view.setVisibility(View.VISIBLE);
      Animation anim = AnimationUtils.loadAnimation(this, animation);
      anim.setDuration(FAST_ANIMATION_DURATION);
      anim.setInterpolator(new DecelerateInterpolator());

      view.startAnimation(anim);
    }
  }

  public void showViewAnimatedOnUiThread(@NonNull final View view, @AnimRes final int animation) {
    this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        BaseActivity.this.showViewAnimated(view, animation);
      }
    });
  }

  public synchronized void hideViewAnimated(@NonNull View view, @AnimRes int animation) {
    if (view == null) return;

    if (view.getVisibility() != View.VISIBLE) return;

    Animation anim = AnimationUtils.loadAnimation(this, animation);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.setDuration(FAST_ANIMATION_DURATION);
    anim.setAnimationListener(new LocalAnimationListener(view));
    view.startAnimation(anim);
  }

  public void hideViewAnimatedOnUiThread(@NonNull final View view, @AnimRes final int animation) {
    this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        BaseActivity.this.hideViewAnimated(view, animation);
      }
    });
  }

  protected static Bitmap getBitmap(Context context, @DrawableRes int image, int sampleSize, float scaleSize) {
    synchronized (images) {
      Bitmap bitmap = images.get(image);

      if(bitmap == null) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        options.inScaled = true;
        options.inDensity = 100;
        options.inTargetDensity = (int) Math.ceil(scaleSize * 100);
        bitmap = BitmapFactory.decodeResource(context.getResources(), image, options);

        images.put(image, bitmap);
      }

      return bitmap;
    }
  }

  public synchronized void startAnimator(@NonNull View view, @AnimatorRes int res) {
    Animator animator = AnimatorInflater.loadAnimator(this, res);
    animator.setTarget(view);
    animator.setDuration(FAST_ANIMATION_DURATION);
    animator.start();
  }

  public void startAnimatorOnUiThread(@NonNull final View view, @AnimatorRes final int res) {
    this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        BaseActivity.this.startAnimator(view, res);
      }
    });
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
