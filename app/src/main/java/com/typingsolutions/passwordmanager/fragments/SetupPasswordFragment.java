package com.typingsolutions.passwordmanager.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.AlertBuilder;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import core.Utils;


public class SetupPasswordFragment extends BaseFragment<SetupActivity> {

  private EditText mEditTextAsEnterPassword;
  private EditText mEditTextAsRepeatPassword;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_2_content_layout, container, false);

    mEditTextAsEnterPassword = (EditText) view.findViewById(R.id.setuplayout_edittext_password);
    mEditTextAsRepeatPassword = (EditText) view.findViewById(R.id.setuplayout_edittext_repeatpassword);

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mEditTextAsEnterPassword.setText("");
    mEditTextAsRepeatPassword.setText("");
  }

  public void getEnteredPassword() {
    if (!arePasswordsEntered()) {
      AlertBuilder.create(getContext())
          .setMessage("You must enter a password")
          .setPositiveButton("ok", (dialog, which) -> retypePassword())
          .show();
    } else if (!checkPasswordsMatch()) {
      AlertBuilder.create(getContext())
          .setMessage("The entered passwords don't match")
          .setPositiveButton("retype", (dialog, which) -> retypePassword())
          .setNegativeButton("dismiss", null)
          .show();
    } else if (!checkPasswordSafety()) {
      AlertBuilder.create(getContext())
          .setMessage("Your password doesn't seem to be safe.\nOpen the help dialog to show what's recommended.")
          .setPositiveButton("change", (dialog, which) -> retypePassword())
          .setNegativeButton("keep it", (dialog, which) -> copyPasswordToActivity())
          .show();
    } else {
      copyPasswordToActivity();
    }
  }

  private void copyPasswordToActivity() {
    getSupportActivity().setPassword(mEditTextAsEnterPassword.getText().toString());
  }

  private boolean checkPasswordsMatch() {
    return mEditTextAsEnterPassword.getText().toString().equals(mEditTextAsRepeatPassword.getText().toString());
  }

  private boolean checkPasswordSafety() {
    return Utils.isSafe(mEditTextAsEnterPassword.getText().toString());
  }

  private boolean arePasswordsEntered() {
    return mEditTextAsEnterPassword.length() > 0 && mEditTextAsRepeatPassword.length() > 0;
  }

  public void retypePassword() {
    mEditTextAsEnterPassword.setText("");
    mEditTextAsRepeatPassword.setText("");
  }
}
