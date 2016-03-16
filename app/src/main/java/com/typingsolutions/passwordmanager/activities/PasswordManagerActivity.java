package com.typingsolutions.passwordmanager.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public abstract class PasswordManagerActivity extends AppCompatActivity {

  private List<BroadcastReceiver> mRegisteredReceiver;

  public PasswordManagerActivity() {
    this.mRegisteredReceiver = new ArrayList<>();
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
   * Registers a {@see BroadcastReceiver} which will be automatically be removed if the {@see PasswordManagerActivity} is destroyed
   *
   * @param receiver the {@see BroadcastReceiver} to listen
   * @param filter to listen at
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
}
