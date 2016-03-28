package com.typingsolutions.passwordmanager.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.callbacks.SetupPasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.SwitchFragmentCallback;
import core.Utils;


public class SetupPasswordFragment extends BaseFragment<SetupActivity> {

  private Button mButtonAsGoBack;
  private Button mButtonAsSetupPassword;
  private EditText mEditTextAsEnterPassword;
  private EditText mEditTextAsRepeatPassword;

  private SwitchFragmentCallback mCallbackAsGoBack;
  private SetupPasswordCallback mCallbackAsSetupPassword;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_2_layout, container, false);

    mCallbackAsGoBack = new SwitchFragmentCallback(getSupportActivity(), SwitchFragmentCallback.Direction.Previous);
    mCallbackAsSetupPassword = new SetupPasswordCallback(getSupportActivity(), this);

    mButtonAsGoBack = (Button) view.findViewById(R.id.setuplayout_button_prev);
    mButtonAsSetupPassword = (Button) view.findViewById(R.id.setuplayout_button_setuppassword);
    mEditTextAsEnterPassword = (EditText) view.findViewById(R.id.setuplayout_edittext_password);
    mEditTextAsRepeatPassword = (EditText) view.findViewById(R.id.setuplayout_edittext_repeatpassword);

    mButtonAsGoBack.setOnClickListener(mCallbackAsGoBack);
    mButtonAsSetupPassword.setOnClickListener(mCallbackAsSetupPassword);

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mEditTextAsEnterPassword.setText("");
    mEditTextAsRepeatPassword.setText("");
  }

  public boolean checkPasswordsMatch() {
    return mEditTextAsEnterPassword.getText().toString().equals(mEditTextAsRepeatPassword.getText().toString());
  }

  public boolean checkPasswordSafety() {
    return Utils.isSafe(mEditTextAsEnterPassword.getText().toString());
  }

  public boolean arePasswordsEntered() {
    return mEditTextAsEnterPassword.length() > 0 && mEditTextAsRepeatPassword.length() > 0;
  }

  public String getPassword() {
    return mEditTextAsEnterPassword.getText().toString();
  }

  public void retypePassword() {
    mEditTextAsEnterPassword.setText("");
    mEditTextAsRepeatPassword.setText("");
  }
}
