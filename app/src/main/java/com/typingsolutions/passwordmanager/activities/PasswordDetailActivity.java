package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.PasswordHistoryAdapter;
import com.typingsolutions.passwordmanager.callbacks.AddPasswordTextWatcher;
import com.typingsolutions.passwordmanager.callbacks.DeletePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.GeneratePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.ToolbarNavigationCallback;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.utils.LinearLayoutManager;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import ui.MaterialView;

public class PasswordDetailActivity extends BaseDatabaseActivity {

  public static final String START_DETAIL_INDEX = "com.typingsolutions.passwordmanager.activities.PasswordDetailActivity.START_DETAIL_INDEX";

  private Toolbar mToolbarAsActionbar;
  private AppBarLayout mAppBarLayoutAsWrapper;
  private EditText mEditTextAsProgram;
  private EditText mEditTextAsUsername;
  private EditText mEditTextAsPassword;
  private CardView mCardviewAsDelete;
  private RecyclerView mRecyclerviewAsPasswordHistory;
  private CollapsingToolbarLayout mCollapsingToolbarLayout;

  private GeneratePasswordCallback mGeneratePasswordCallback;

  private AddPasswordTextWatcher mUsernameTextWatcher;
  private AddPasswordTextWatcher mProgramTextWatcher;
  private AddPasswordTextWatcher mPasswordTextWatcher;
  private PasswordContainer mCurrentPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_detail_layout);

    mToolbarAsActionbar = findCastedViewById(R.id.passworddetaillayout_toolbar_actionbar);
    mEditTextAsProgram = findCastedViewById(R.id.passworddetaillayout_edittext_program);
    mEditTextAsUsername = findCastedViewById(R.id.passworddetaillayout_edittext_username);
    mEditTextAsPassword = findCastedViewById(R.id.passworddetaillayout_edittext_password);
    mCardviewAsDelete = findCastedViewById(R.id.passworddetaillayout_cardview_delete);
    mAppBarLayoutAsWrapper = findCastedViewById(R.id.passworddetaillayout_appbarlayout_wrapper);
    mRecyclerviewAsPasswordHistory = findCastedViewById(R.id.passworddetaillayout_recyclerview_passwordhistory);
    mCollapsingToolbarLayout = findCastedViewById(R.id.passworddetaillayout_collapsiontoolbarlayout_wrapper);
    Button button = findCastedViewById(R.id.passworddetaillayout_button_generatepassword);
    TextView nohistory = findCastedViewById(R.id.passworddetaillayout_textview_nohistory);

    setSupportActionBar(mToolbarAsActionbar);
    mToolbarAsActionbar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));

    int passwordId = getIntent().getIntExtra(START_DETAIL_INDEX, -1);
    if (passwordId == -1) return;

    for (int i = 0; i < containerCount(); i++) {
      PasswordContainer iterator = (PasswordContainer) getContainerAt(i);
      if (iterator.getId() == passwordId) {
        mCurrentPassword = iterator;
        break;
      }
    }

    mRecyclerviewAsPasswordHistory.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerviewAsPasswordHistory.setAdapter(new PasswordHistoryAdapter(this, mCurrentPassword));

    mGeneratePasswordCallback = new GeneratePasswordCallback(this, mEditTextAsPassword);
    button.setOnClickListener(mGeneratePasswordCallback);

    String programString = mCurrentPassword.getProgram();
    mProgramTextWatcher = new AddPasswordTextWatcher(this, programString, true);
    mEditTextAsProgram.setText(programString);
    mEditTextAsProgram.addTextChangedListener(mProgramTextWatcher);

    String usernameString = mCurrentPassword.getUsername();
    mUsernameTextWatcher = new AddPasswordTextWatcher(this, usernameString, false);
    mEditTextAsUsername.setText(usernameString);
    mEditTextAsUsername.addTextChangedListener(mUsernameTextWatcher);

    String passwordString = mCurrentPassword.getDefaultPassword();
    mPasswordTextWatcher = new AddPasswordTextWatcher(this, passwordString, true);
    mEditTextAsPassword.setText(passwordString);
    mEditTextAsPassword.addTextChangedListener(mPasswordTextWatcher);

    DeletePasswordCallback onClickListener = new DeletePasswordCallback(this, mCurrentPassword);
    mCardviewAsDelete.setOnClickListener(onClickListener);

    mCollapsingToolbarLayout.setTitle(programString);
    mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
//    ViewCompat.setElevation(mAppBarLayoutAsWrapper, 10);
//    ViewCompat.setElevation(mCollapsingToolbarLayout, getResources().getDimension(R.dimen.dimen_sm));

    if (!isTablet()) {
      TextView textViewAsHeader = findCastedViewById(R.id.passworddetaillayout_textview_header);
      ViewUtils.setColor(textViewAsHeader, programString, passwordString);
    } else {
      MaterialView v = findCastedViewById(R.id.passworddetaillayout_view_artboard);
      v.setOverlayColor(ViewUtils.setColor(null, programString, passwordString));
    }

    if (mCurrentPassword.getPasswordItems().size() > 1) {
      nohistory.setVisibility(View.GONE);
      mRecyclerviewAsPasswordHistory.setVisibility(View.VISIBLE);
      mRecyclerviewAsPasswordHistory.setNestedScrollingEnabled(false);
      mRecyclerviewAsPasswordHistory.setEnabled(false);
    }

    button.requestFocus();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.password_detail_menu, menu);
    setMenuItemEnabled(mToolbarAsActionbar, 0, false);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() != R.id.createusermenu_item_done) return false;

    try {
      if (mPasswordTextWatcher.needUpdate()) {
        mCurrentPassword.addItem(mEditTextAsPassword.getText().toString());
        changeContainerItem(indexOfContainer(mCurrentPassword), mCurrentPassword);
      }


      String program = mProgramTextWatcher.needUpdate() ? mEditTextAsProgram.getText().toString() : null;
      String username = mUsernameTextWatcher.needUpdate() ? mEditTextAsUsername.getText().toString() : null;
      mCurrentPassword.update(-1, program, username);
      if(mProgramTextWatcher.needUpdate() || mUsernameTextWatcher.needUpdate()) {
        changeContainerItem(indexOfContainer(mCurrentPassword), mCurrentPassword);
      }
    } catch (Exception e) {
      showErrorLog(getClass(), e);
    }

    onBackPressed();

    return super.onOptionsItemSelected(item);
  }


  @Override
  protected View getSnackbarRelatedView() {
    return null;
  }

  @Override
  protected void onActivityChange() {
    // TODO:
    Log.d(getClass().getSimpleName(), "asdf");
  }

  public void enableSave(boolean enable) {
    setMenuItemEnabled(mToolbarAsActionbar, 0, enable);
  }


}
