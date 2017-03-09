package com.typingsolutions.kore.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.typingsolutions.kore.R;


public class ExtendSetupFragment extends Fragment implements IPasswordProvider {

  private TextInputEditText mEditTextAsEnterPassword;
  private TextInputEditText mEditTextAsRepeatPassword;
  private TextInputEditText mEditTextAsEnterPIM;
  private TextInputEditText mEditTextAsRepeatPIM;
  private TextView mTextViewAsPIMHint;

  private String mBackupText;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.setup_fragment_3, container, false);
    mEditTextAsEnterPassword = (TextInputEditText) root.findViewById(R.id.setuplayout_edittext_passwordenter);
    mEditTextAsRepeatPassword = (TextInputEditText) root.findViewById(R.id.setuplayout_edittext_passwordrepeat);

    mEditTextAsEnterPIM = (TextInputEditText) root.findViewById(R.id.setuplayout_edittext_pimenterextended);
    mEditTextAsRepeatPIM = (TextInputEditText) root.findViewById(R.id.setuplayout_edittext_pimrepeatextended);

    mTextViewAsPIMHint = (TextView) root.findViewById(R.id.setuplayout_textview_currentpim);

    mEditTextAsEnterPassword.addTextChangedListener(new EnableSetupTextWatcher((SetupActivity)getActivity(), this, mEditTextAsRepeatPassword, true));
    mEditTextAsRepeatPassword.addTextChangedListener(new EnableSetupTextWatcher((SetupActivity)getActivity(), this, mEditTextAsEnterPassword));

    mEditTextAsEnterPIM.addTextChangedListener(new EnableSetupTextWatcher((SetupActivity)getActivity(), mEditTextAsRepeatPIM));
    mEditTextAsRepeatPIM.addTextChangedListener(new EnableSetupTextWatcher((SetupActivity)getActivity(), mEditTextAsEnterPIM));

    return root;
  }

  @Override
  public void setPasswords(CharSequence pw1, CharSequence pw2) {
    mEditTextAsEnterPassword.setText(pw1);
    mEditTextAsRepeatPassword.setText(pw2);
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

    activity.clearText(mEditTextAsEnterPIM);
    activity.clearText(mEditTextAsRepeatPIM);

    mEditTextAsEnterPIM.clearComposingText();
    mEditTextAsRepeatPIM.clearComposingText();

    mEditTextAsEnterPIM = null;
    mEditTextAsRepeatPIM = null;
  }

  @Override
  public CharSequence getPassword1() {
    return mEditTextAsEnterPassword.getText();
  }

  @Override
  public CharSequence getPassword2() {
    return mEditTextAsRepeatPassword.getText();
  }

  CharSequence getPIM1() {
    return mEditTextAsEnterPIM.getText();
  }

  CharSequence getPIM2() {
    return mEditTextAsRepeatPIM.getText();
  }

  void setCurrentPIM() {
    if (mBackupText == null) {
      mBackupText = mTextViewAsPIMHint.getText().toString();
    }

    SetupActivity activity = (SetupActivity) getActivity();

    int calcPim = activity.mKoreApplication.calculatePIM(mEditTextAsEnterPassword.getText().toString());
    mTextViewAsPIMHint.setText(mBackupText.replace("${_pim_}", Integer.toString(calcPim)));
  }
}
