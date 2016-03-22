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

public class SetupPasswordFragment extends BaseFragment<SetupActivity> {
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_2_layout, container, false);
    Button b = (Button) view.findViewById(R.id.setuplayout_button_prev);

    SwitchFragmentCallback callback = new SwitchFragmentCallback(getSupportActivity(), R.id.setuplayout_fragment_wrapper,
        new SetupWelcomeFragment(), R.anim.slide_in_left, R.anim.slide_out_right);
    b.setOnClickListener(callback);
    return view;
  }

}
