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
import com.typingsolutions.passwordmanager.callbacks.SetupPimCallback;
import com.typingsolutions.passwordmanager.callbacks.SwitchFragmentCallback;

public class SetupPimFragment extends BaseFragment<SetupActivity> {

  private EditText mEditTextAsPim;
  private Button mButtonAsPrevious;
  private Button mButtonAsDone;

  private SwitchFragmentCallback mPreviousCallback;
  private SetupPimCallback mSetupPimCallback;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.setup_3_layout, container, false);

    mPreviousCallback = new SwitchFragmentCallback(getSupportActivity(), SwitchFragmentCallback.Direction.Previous);
    mSetupPimCallback = new SetupPimCallback(getSupportActivity(), this);

    mEditTextAsPim = (EditText) view.findViewById(R.id.setuplayout_edittext_pim);
    mButtonAsDone = (Button) view.findViewById(R.id.setuplayout_button_setuppim);
    mButtonAsPrevious = (Button) view.findViewById(R.id.setuplayout_button_pimprev);

    mButtonAsPrevious.setOnClickListener(mPreviousCallback);
    mButtonAsDone.setOnClickListener(mSetupPimCallback);

    return view;
  }
}
