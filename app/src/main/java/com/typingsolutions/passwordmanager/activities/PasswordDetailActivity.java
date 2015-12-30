package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.PasswordHistoryAdapter;
import com.typingsolutions.passwordmanager.callbacks.click.DeletePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.click.GeneratePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.click.ToolbarNavigationCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.AddPasswordTextWatcher;
import com.typingsolutions.passwordmanager.utils.LinearLayoutManager;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import core.data.Password;
import core.data.PasswordProvider;

public class PasswordDetailActivity extends AppCompatActivity {

  public static final String START_DETAIL_INDEX = "com.typingsolutions.passwordmanager.activities.PasswordDetailActivity.START_DETAIL_INDEX";

  private Toolbar toolbar;
  private EditText program;
  private EditText username;
  private EditText password;
  private CardView delete;
  private RecyclerView passwordHistory;

  private RecyclerView.LayoutManager layoutManager;
  private PasswordHistoryAdapter passwordHistoryAdapter;

  private AddPasswordTextWatcher usernameTextWatcher;
  private AddPasswordTextWatcher programTextWatcher;
  private AddPasswordTextWatcher passwordTextWatcher;

  private int passwordId;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private TextView header;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_detail_layout);

    toolbar = (Toolbar) findViewById(R.id.passworddetaillayout_toolbar_actionbar);
    program = (EditText) findViewById(R.id.passworddetaillayout_edittext_program);
    username = (EditText) findViewById(R.id.passworddetaillayout_edittext_username);
    password = (EditText) findViewById(R.id.passworddetaillayout_edittext_password);
    header = (TextView) findViewById(R.id.passworddetaillayout_textview_header);
    delete = (CardView) findViewById(R.id.passworddetaillayout_cardview_delete);
    passwordHistory = (RecyclerView) findViewById(R.id.passworddetaillayout_recyclerview_passwordhistory);
    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.passworddetaillayout_collapsiontoolbarlayout_wrapper);
    Button button = (Button) findViewById(R.id.passworddetaillayout_button_generatepassword);
    TextView nohistory = (TextView) findViewById(R.id.passworddetaillayout_textview_nohistory);

    setSupportActionBar(toolbar);
    toolbar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));

    passwordId = getIntent().getIntExtra(START_DETAIL_INDEX, -1);
    if (passwordId == -1) return;
    Password currentPassword = PasswordProvider.getInstance(this).getById(passwordId);

    layoutManager = new LinearLayoutManager(this);
    passwordHistoryAdapter = new PasswordHistoryAdapter(this, passwordId);
    passwordHistory.setLayoutManager(layoutManager);
    passwordHistory.setAdapter(passwordHistoryAdapter);

    button.setOnClickListener(new GeneratePasswordCallback(this, password));

    String programString = currentPassword.getProgram();
    programTextWatcher = new AddPasswordTextWatcher(this, programString, true);
    program.setText(programString);
    program.addTextChangedListener(programTextWatcher);

    String usernameString = currentPassword.getUsername();
    usernameTextWatcher = new AddPasswordTextWatcher(this, usernameString, false);
    username.setText(usernameString);
    username.addTextChangedListener(usernameTextWatcher);

    String passwordString = currentPassword.getFirstItem();
    passwordTextWatcher = new AddPasswordTextWatcher(this, passwordString, true);
    password.setText(passwordString);
    password.addTextChangedListener(passwordTextWatcher);

    DeletePasswordCallback onClickListener = new DeletePasswordCallback(this, currentPassword, this);
    delete.setOnClickListener(onClickListener);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

    collapsingToolbarLayout.setTitle(programString);
    collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

    ViewUtils.setColor(header, programString, passwordString);

    if(currentPassword.getHistoryCount() > 1) {
      nohistory.setVisibility(View.GONE);
      passwordHistory.setVisibility(View.VISIBLE);
      passwordHistory.setNestedScrollingEnabled(false);
      passwordHistory.setEnabled(false);
    }

    // TODO:
    if (!toolbar.requestFocus())
      toolbar.requestFocus();
  }


  @Override
  protected void onPause() {
    super.onPause();
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.password_detail_menu, menu);
    switchMenuState(false);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() != R.id.createusermenu_item_done) return false;

    String newUsername = null;
    String newProgram = null;
    String newPassword = null;
    try {
      newUsername = username.getText().toString();
      newProgram = program.getText().toString();
      newPassword = passwordTextWatcher.needUpdate() ? password.getText().toString() : null;
    } catch (Exception e) {
      // ignored
    }

    try {
      if (newPassword != null) {
        PasswordProvider.getInstance(this).editPassword(passwordId, newPassword);
      }

      PasswordProvider.getInstance(this).editPassword(passwordId, newProgram, newUsername);
    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }

    onBackPressed();

    return super.onOptionsItemSelected(item);
  }

  public void switchMenuState(boolean state) {
    try {
      MenuItem item = toolbar.getMenu().getItem(0);
      item.setEnabled(state);
      item.getIcon().setAlpha(state ? 255 : 64);
    } catch (Exception e) {
      // ignored
    }
  }
}
