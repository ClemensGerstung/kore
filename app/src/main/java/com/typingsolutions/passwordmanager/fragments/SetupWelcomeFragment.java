package com.typingsolutions.passwordmanager.fragments;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.callbacks.SwitchFragmentCallback;

public class SetupWelcomeFragment extends BaseFragment<SetupActivity> {


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_1_layout, container, false);

    Button b = (Button) view.findViewById(R.id.setuplayout_button_nextToPassword);
    SwitchFragmentCallback callback = new SwitchFragmentCallback(getSupportActivity(), R.id.setuplayout_fragment_wrapper,
        new SetupPasswordFragment(), R.anim.slide_in_right, R.anim.slide_out_left);
    b.setOnClickListener(callback);

    return view;
  }
}
