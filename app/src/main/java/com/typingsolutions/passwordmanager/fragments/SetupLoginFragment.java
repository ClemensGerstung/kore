package com.typingsolutions.passwordmanager.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.callbacks.SetupLoginCallback;
import com.typingsolutions.passwordmanager.callbacks.SwitchFragmentCallback;

public class SetupLoginFragment extends BaseFragment<SetupActivity> {

  private Button mButtonAsBack;
  private Button mButtonAsLogin;
  private EditText mEditTextAsPassword;
  private EditText mEditTextAsPim;

  private SwitchFragmentCallback mGoBackCallback;
  private SetupLoginCallback mLoginCallback;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_4_layout, container, false);

    mGoBackCallback = new SwitchFragmentCallback(getSupportActivity(), SwitchFragmentCallback.Direction.Previous);
    mLoginCallback = new SetupLoginCallback(getSupportActivity(), this);

    mButtonAsBack = (Button) view.findViewById(R.id.setuplayout_button_loginprev);
    mButtonAsLogin = (Button) view.findViewById(R.id.setuplayout_button_loginpassword);
    mEditTextAsPassword = (EditText) view.findViewById(R.id.setuplayout_edittext_loginpassword);
    mEditTextAsPim = (EditText) view.findViewById(R.id.setuplayout_edittext_loginpim);

    mButtonAsBack.setOnClickListener(mGoBackCallback);
    mButtonAsLogin.setOnClickListener(mLoginCallback);

    return view;
  }

  public boolean checkPassword() {
    return getSupportActivity().getPassword().equals(mEditTextAsPassword.getText().toString());
  }

  public boolean checkPim() {
    int currentPim = Integer.parseInt(mEditTextAsPim.getText().toString());

    return currentPim == getSupportActivity().getPim();
  }

  public Bundle getBundle() {
    Bundle bundle = new Bundle();
    int currentPim = Integer.parseInt(mEditTextAsPim.getText().toString());
    bundle.putInt("pim", currentPim);
    return bundle;
  }
}
