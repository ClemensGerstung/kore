package com.typingsolutions.passwordmanager.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.callbacks.SetupPasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.SwitchFragmentCallback;

public class SetupPasswordFragment extends BaseFragment<SetupActivity> {
  private static final SetupWelcomeFragment SETUP_WELCOME_FRAGMENT = new SetupWelcomeFragment();

  private Button mButtonAsGoBack;
  private Button mButtonAsSetupPassword;

  private SwitchFragmentCallback mCallbackAsGoBack;
  private SetupPasswordCallback mCallbackAsSetupPassword;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_2_layout, container, false);

    mCallbackAsGoBack = new SwitchFragmentCallback(getSupportActivity(), R.id.setuplayout_fragment_wrapper, SETUP_WELCOME_FRAGMENT, R.anim.slide_in_left, R.anim.slide_out_right);
    mCallbackAsSetupPassword = new SetupPasswordCallback(getSupportActivity(), R.id.setuplayout_fragment_wrapper, null, R.anim.slide_in_right, R.anim.slide_out_left);

    mButtonAsGoBack = (Button) view.findViewById(R.id.setuplayout_button_prev);
    mButtonAsSetupPassword = (Button) view.findViewById(R.id.setuplayout_button_setuppassword);

    mButtonAsGoBack.setOnClickListener(mCallbackAsGoBack);
    mButtonAsSetupPassword.setOnClickListener(mCallbackAsSetupPassword);

    return view;
  }

}
