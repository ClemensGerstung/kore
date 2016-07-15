package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
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
import com.typingsolutions.passwordmanager.callbacks.ToolbarNavigationCallback;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.utils.LinearLayoutManager;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import core.data.PasswordProvider;

public class PasswordDetailActivity extends BaseDatabaseActivity {

  public static final String START_DETAIL_INDEX = "com.typingsolutions.passwordmanager.activities.PasswordDetailActivity.START_DETAIL_INDEX";

  private Toolbar mToolbarAsActionbar;
  private EditText mEditTextAsProgram;
  private EditText mEditTextAsUsername;
  private EditText mEditTextAsPassword;
  private CardView mCardviewAsDelete;
  private RecyclerView mRecyclerviewAsPasswordHistory;
  private CollapsingToolbarLayout mCollapsingToolbarLayout;
  private TextView mTextViewAsHeader;

  private AddPasswordTextWatcher mUsernameTextWatcher;
  private AddPasswordTextWatcher mProgramTextWatcher;
  private AddPasswordTextWatcher mPasswordTextWatcher;
  private PasswordContainer mCurrentPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_detail_layout);

    mToolbarAsActionbar = (Toolbar) findViewById(R.id.passworddetaillayout_toolbar_actionbar);
    mEditTextAsProgram = (EditText) findViewById(R.id.passworddetaillayout_edittext_program);
    mEditTextAsUsername = (EditText) findViewById(R.id.passworddetaillayout_edittext_username);
    mEditTextAsPassword = (EditText) findViewById(R.id.passworddetaillayout_edittext_password);
    mTextViewAsHeader = (TextView) findViewById(R.id.passworddetaillayout_textview_header);
    mCardviewAsDelete = (CardView) findViewById(R.id.passworddetaillayout_cardview_delete);
    mRecyclerviewAsPasswordHistory = (RecyclerView) findViewById(R.id.passworddetaillayout_recyclerview_passwordhistory);
    mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.passworddetaillayout_collapsiontoolbarlayout_wrapper);
    Button button = (Button) findViewById(R.id.passworddetaillayout_button_generatepassword);
    TextView nohistory = (TextView) findViewById(R.id.passworddetaillayout_textview_nohistory);

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

    //button.setOnClickListener(new GeneratePasswordCallback(this, mTextViewAsPassword));

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

//    DeletePasswordCallback onClickListener = new DeletePasswordCallback(this, mCurrentPassword, this);
    //mCardviewAsDelete.setOnClickListener(onClickListener);

    if (!debug)
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

    mCollapsingToolbarLayout.setTitle(programString);
    mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
    ViewCompat.setElevation(mCollapsingToolbarLayout, getResources().getDimension(R.dimen.dimen_sm));

    ViewUtils.setColor(mTextViewAsHeader, programString, passwordString);

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

//      PasswordProvider.getInstance(this).editPassword(mPasswordId, newProgram, newUsername);
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

    Log.d(getClass().getSimpleName(), "asdf");
  }

  public void enableSave(boolean enable){
    setMenuItemEnabled(mToolbarAsActionbar, 0, enable);
  }
}
