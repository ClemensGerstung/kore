package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import core.data.PasswordProvider;

public class CreatePasswordActivity extends BaseDatabaseActivity {

  private Toolbar mToolbarAsActionBar;
  private AppBarLayout mAppBarLayoutAsWrapperForToolbarAsActionBar;
  private EditText mEditTextAsProgram;
  private NestedScrollView mNestedScrollViewAsWrapperForInput;
  private EditText mEditTextAsUsername;
  private EditText mEditTextAsPassword;
  private Button mButtonAsGenerateRandomPassword;

  private final TextWatcher switchTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      boolean result = mEditTextAsProgram.length() > 0
          && mEditTextAsPassword.length() > 0;
      setMenuItemEnabled(mToolbarAsActionBar, 0, result);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
  };

  private final NestedScrollView.OnScrollChangeListener scrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
      float elevation = 0;
      if (scrollY > 0) {
        elevation = CreatePasswordActivity.this.getResources().getDimension(R.dimen.dimen_sm);
      } else if (scrollY <= 0) {
        elevation = CreatePasswordActivity.this.getResources().getDimension(R.dimen.zero);
      }

      ViewCompat.setElevation(mAppBarLayoutAsWrapperForToolbarAsActionBar, elevation);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_password_layout);

    mAppBarLayoutAsWrapperForToolbarAsActionBar = findCastedViewById(R.id.createpasswordlayout_appbar_wrapper);
    mToolbarAsActionBar = (Toolbar) mAppBarLayoutAsWrapperForToolbarAsActionBar.findViewById(R.id.createpasswordlayout_toolbar_actionbar);
    mEditTextAsProgram = (EditText) mAppBarLayoutAsWrapperForToolbarAsActionBar.findViewById(R.id.createpasswordlayout_edittext_program);
    mNestedScrollViewAsWrapperForInput = findCastedViewById(R.id.createpasswordlayout_nestedscrollview_wrapper);
    mEditTextAsUsername = (EditText) mNestedScrollViewAsWrapperForInput.findViewById(R.id.createpasswordlayout_edittext_username);
    mEditTextAsPassword = (EditText) mNestedScrollViewAsWrapperForInput.findViewById(R.id.createpasswordlayout_edittext_password);
    mButtonAsGenerateRandomPassword = (Button) mNestedScrollViewAsWrapperForInput.findViewById(R.id.createpasswordlayout_button_generatepassword);

    setSupportActionBar(mToolbarAsActionBar);
//    mToolbarAsActionBar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));

    mEditTextAsProgram.addTextChangedListener(switchTextWatcher);
    mEditTextAsPassword.addTextChangedListener(switchTextWatcher);

//    mButtonAsGenerateRandomPassword.setOnClickListener(new GeneratePasswordCallback(this, mEditTextAsPassword));

    mNestedScrollViewAsWrapperForInput.setOnScrollChangeListener(scrollChangeListener);
    mNestedScrollViewAsWrapperForInput.requestFocus();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.create_password_menu, menu);
    setMenuItemEnabled(mToolbarAsActionBar, 0, false);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id != R.id.createusermenu_item_done) return false;

    String program = mEditTextAsProgram.getText().toString();
    String username = mEditTextAsUsername.getText().toString();
    String password = mEditTextAsPassword.getText().toString();

    try {
      PasswordProvider.getInstance(this).addPassword(program, username, password);
    } catch (Exception e) {
      showErrorLog(getClass(), e);
    } finally {
      onBackPressed();
    }

    return true;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  @Override
  protected View getSnackbarRelatedView() {
    return mToolbarAsActionBar;
  }
}
