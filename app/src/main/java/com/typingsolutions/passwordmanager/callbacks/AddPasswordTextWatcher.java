package com.typingsolutions.passwordmanager.callbacks;

import android.support.annotation.Nullable;
import com.typingsolutions.passwordmanager.BaseTextWatcher;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;

public class AddPasswordTextWatcher extends BaseTextWatcher<PasswordDetailActivity> {

  private String mInitialValue;
  private boolean mUpdate;
  private boolean mCheckHasText;


  public AddPasswordTextWatcher(PasswordDetailActivity activity, @Nullable String initialValue, boolean checkHasText) {
    super(activity);
    this.mInitialValue = initialValue;
    this.mCheckHasText = checkHasText;
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    mUpdate = !s.toString().equals(mInitialValue);
    if (mUpdate) {
      if (mCheckHasText) {
        mActivity.enableSave(s.length() > 0);
        mUpdate &= s.length() > 0;
      } else {
        mActivity.enableSave(true);
      }
    } else {
      mActivity.enableSave(false);
    }
  }

  public boolean needUpdate() {
    return mUpdate;
  }
}
