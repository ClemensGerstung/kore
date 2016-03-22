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
import com.typingsolutions.passwordmanager.callbacks.SwitchFragmentCallback;

public class SetupWelcomeFragment extends BaseFragment<SetupActivity> {
  private static final SetupPasswordFragment SETUP_PASSWORD_FRAGMENT = new SetupPasswordFragment();

  private Button mButtonAsNext;
  private SwitchFragmentCallback mCallbackAsNext;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_1_layout, container, false);

    mCallbackAsNext = new SwitchFragmentCallback(getSupportActivity(), R.id.setuplayout_fragment_wrapper, SETUP_PASSWORD_FRAGMENT, R.anim.slide_in_right, R.anim.slide_out_left);
    mButtonAsNext = (Button) view.findViewById(R.id.setuplayout_button_nextToPassword);
    mButtonAsNext.setOnClickListener(mCallbackAsNext);

    return view;
  }
}
