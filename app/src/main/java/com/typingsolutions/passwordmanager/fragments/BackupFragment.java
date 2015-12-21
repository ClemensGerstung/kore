package com.typingsolutions.passwordmanager.fragments;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.EditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;

public class BackupFragment extends Fragment {
  private Button doBackup;
  private ImageButton expand;
  private TextInputLayout passwordWrapper;
  private EditText editText_password;
  private TextInputLayout repeatPasswordWrapper;
  private EditText editText_repeatPassword;
  private TextView hint;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.backup_layout, container, false);
    doBackup = (Button) root.findViewById(R.id.backuplayout_button_dobackup);
    expand = (ImageButton) root.findViewById(R.id.backuplayout_imagebutton_expand);
    passwordWrapper = (TextInputLayout) root.findViewById(R.id.backuplayout_textinputlayout_passwordwrapper);
    repeatPasswordWrapper = (TextInputLayout) root.findViewById(R.id.backuplayout_textinputlayout_repeatpasswordwrapper);
    hint = (TextView) root.findViewById(R.id.backuplayout_textview_hint);

    expand.setOnClickListener(new ExpandCallback(getActivity(), this));

    return root;
  }

  public TextInputLayout getPasswordWrapper() {
    return passwordWrapper;
  }

  public TextInputLayout getRepeatPasswordWrapper() {
    return repeatPasswordWrapper;
  }

  public TextView getHint() {
    return hint;
  }

  class ExpandCallback extends BaseCallback {

    private BackupFragment fragment;
    private boolean expanded;

    public ExpandCallback(Context context, BackupFragment fragment) {
      super(context);
      this.fragment = fragment;
      expanded = false;
    }

    @Override
    public void setValues(Object... values) {

    }

    @Override
    public void onClick(View v) {
      v.animate()
          .rotation(0)
          .rotationBy(180)
          .setDuration(150)
          .setInterpolator(new DecelerateInterpolator())
          .setListener(new ExpandAnimationListenerImplementation(v))
          .start();

      if (expanded) {
        fragment.getHint().setVisibility(View.GONE);
        fragment.getPasswordWrapper().setVisibility(View.GONE);
        fragment.getRepeatPasswordWrapper().setVisibility(View.GONE);
      } else {
        fragment.getHint().setVisibility(View.VISIBLE);
        fragment.getPasswordWrapper().setVisibility(View.VISIBLE);
        fragment.getRepeatPasswordWrapper().setVisibility(View.VISIBLE);
      }
    }

    private class ExpandAnimationListenerImplementation implements Animator.AnimatorListener {
      private View view;


      public ExpandAnimationListenerImplementation(View view) {
        this.view = view;
      }

      @Override
      public void onAnimationStart(Animator animation) {
        view.setClickable(false);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        view.setClickable(true);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    }
  }
}
