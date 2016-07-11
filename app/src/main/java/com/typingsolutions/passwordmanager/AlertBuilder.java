package com.typingsolutions.passwordmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class AlertBuilder {
  private static AlertBuilder builder = new AlertBuilder();

  private AlertDialog mAlertDialog;
  private CharSequence mPositiveText = null;
  private CharSequence mNegativeText = null;
  private CharSequence mNeutralText = null;


  public static AlertBuilder create(Context context) {
    return create(context, R.style.Base_AlertDialog_LoginStyle);
  }

  public static AlertBuilder create(Context context, @StyleRes int theme) {
    builder.mAlertDialog = new AlertDialog.Builder(context, theme).create();
    return builder;
  }

  public static AlertDialog getLastCreated() {
    return builder.mAlertDialog;
  }

  public AlertBuilder setPositiveButton(CharSequence text) {
    mPositiveText = text;
    return this;
  }

  public AlertBuilder setNegativeButton(CharSequence text) {
    mNegativeText = text;
    return this;
  }

  public AlertBuilder setNeutralButton(CharSequence text) {
    mNeutralText = text;
    return this;
  }

  /**
   * Need to be called after {@see setPositiveButton}, {@see setNegativeButton} or {@see setNeutralButton}
   *
   * @param callback BaseDialogCallback
   * @return AlertBuilder
   */
  public AlertBuilder setCallback(@NonNull BaseDialogCallback<? extends BaseActivity> callback) {
    mAlertDialog.setOnShowListener(callback);

    if (mPositiveText != null) {
      mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mPositiveText, callback);
    }

    if (mNegativeText != null) {
      mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mNegativeText, callback);
    }

    if (mNeutralText != null) {
      mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mNeutralText, callback);
    }

    return this;
  }

  public AlertBuilder setView(@LayoutRes int layout) {
    return setView(View.inflate(mAlertDialog.getContext(), layout, null));
  }

  public AlertBuilder setView(View view) {
    mAlertDialog.setView(view);
    return this;
  }

  public AlertBuilder setSecurityFlags() {
    mAlertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    return this;
  }

  public AlertBuilder setTitle(CharSequence title) {
    mAlertDialog.setTitle(title);
    return this;
  }

  public AlertBuilder setMessage(CharSequence message) {
    mAlertDialog.setMessage(message);
    return this;
  }

  public void show() {
    mAlertDialog.show();
  }
}
