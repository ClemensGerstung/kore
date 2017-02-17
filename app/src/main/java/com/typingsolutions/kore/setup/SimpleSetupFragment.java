package com.typingsolutions.kore.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.typingsolutions.kore.R;

public class SimpleSetupFragment extends Fragment implements IPasswordProvider {
  private TextInputEditText mEditTextAsEnterPassword;
  private TextInputEditText mEditTextAsRepeatPassword;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.setup_fragment_2, container, false);
    mEditTextAsEnterPassword = (TextInputEditText) root.findViewById(R.id.setuplayout_edittext_passwordenter);
    mEditTextAsRepeatPassword = (TextInputEditText) root.findViewById(R.id.setuplayout_edittext_passwordrepeat);

    mEditTextAsEnterPassword.addTextChangedListener(new EnableSetupTextWatcher((SetupActivity)getActivity(), mEditTextAsRepeatPassword));
    mEditTextAsRepeatPassword.addTextChangedListener(new EnableSetupTextWatcher((SetupActivity)getActivity(), mEditTextAsEnterPassword));

    return root;
  }

  @Override
  public CharSequence getPassword1() {
    return mEditTextAsEnterPassword.getText();
  }

  @Override
  public CharSequence getPassword2() {
    return mEditTextAsRepeatPassword.getText();
  }

  @Override
  public void setPasswords(CharSequence pw1, CharSequence pw2) {

  }

  @Override
  public void cleanUp() {
    SetupActivity activity = (SetupActivity) getActivity();

    activity.clearText(mEditTextAsEnterPassword);
    activity.clearText(mEditTextAsRepeatPassword);

    mEditTextAsEnterPassword.clearComposingText();
    mEditTextAsRepeatPassword.clearComposingText();

    mEditTextAsEnterPassword = null;
    mEditTextAsRepeatPassword = null;
  }
}
