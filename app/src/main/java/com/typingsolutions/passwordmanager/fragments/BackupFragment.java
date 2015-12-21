package com.typingsolutions.passwordmanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.EditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import com.typingsolutions.passwordmanager.callbacks.click.ExpandCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackupFragment extends Fragment {
  public static final int BACKUP_REQUEST_CODE = 36;

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
    doBackup.setOnClickListener(new DoBackupCallback(getActivity()));

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

  class DoBackupCallback extends BaseCallback {


    public DoBackupCallback(Context context) {
      super(context);
    }

    @Override
    public void setValues(Object... values) {

    }

    @Override
    public void onClick(View v) {
      Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);

      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
      String fileName = String.format("password-manager-backup-%s.encrypt", dateFormat.format(new Date()));
      intent.putExtra(Intent.EXTRA_TITLE, fileName);
      intent.setType("*/*");
      ((Activity) context).startActivityForResult(intent, BACKUP_REQUEST_CODE);

    }
  }
}
