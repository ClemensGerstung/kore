package com.typingsolutions.passwordmanager;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class BaseTextWatcher<TActivity extends BaseActivity> extends BaseCallback<TActivity>
    implements TextWatcher {

  public BaseTextWatcher(TActivity activity) {
    super(activity);
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {

  }

  @Override
  public void setValues(Object... values) {

  }
}
