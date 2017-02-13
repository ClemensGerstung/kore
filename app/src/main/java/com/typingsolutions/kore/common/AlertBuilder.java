package com.typingsolutions.kore.common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.typingsolutions.kore.R;

public class AlertBuilder {
  private static AlertBuilder builder = new AlertBuilder();

  private AlertDialog mAlertDialog;

  public static AlertBuilder create(Context context) {
    return create(context, R.style.ColoredAlertDialog);
  }

  public static AlertBuilder create(Context context, @StyleRes int theme) {
    builder.mAlertDialog = new AlertDialog.Builder(context, theme).create();
    return builder;
  }

  public static AlertDialog getLastCreated() {
    return builder.mAlertDialog;
  }

  public AlertBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
    mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, text, listener);
    return this;
  }

  public AlertBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
    mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, text, listener);
    return this;
  }

  public AlertBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
    mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, text, listener);
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

  public AlertBuilder setMessage(@StringRes int message) {
    mAlertDialog.setMessage(getDialog().getContext().getResources().getString(message));

    return this;
  }

  public AlertDialog getDialog() {
    return mAlertDialog;
  }

  public AlertBuilder setItems(AlertBuilder.OnItemClickListener itemClickListener, String... items) {
    Context context = getDialog().getContext();
    ListView lv = new ListView(context);
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    int dimension = (int) context.getResources().getDimension(R.dimen.md);
    lv.setLayoutParams(params);
    lv.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items));
    lv.setOnItemClickListener((adapterView, view, i, l) -> itemClickListener.onItemClick(mAlertDialog, adapterView, view, i, l));

    LinearLayout layout = new LinearLayout(context);
    layout.addView(lv);
    layout.setPadding(dimension, dimension, dimension, dimension);

    return setView(layout);
  }

  public void show() {
    mAlertDialog.show();
  }

  public interface OnItemClickListener {
    void onItemClick(DialogInterface dialog, AdapterView<?> parent, View view, int position, long id);
  }
}
