package com.typingsolutions.passwordmanager.activities;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import com.typingsolutions.passwordmanager.utils.LinearLayoutManager;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.click.DeletePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.click.GeneratePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.AddPasswordTextWatcher;
import com.typingsolutions.passwordmanager.adapter.PasswordHistoryAdapter;
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_detail_layout);

    toolbar = (Toolbar) findViewById(R.id.passworddetaillayout_toolbar_actionbar);
    program = (EditText) findViewById(R.id.passworddetaillayout_edittext_program);
    username = (EditText) findViewById(R.id.passworddetaillayout_edittext_username);
    password = (EditText) findViewById(R.id.passworddetaillayout_edittext_password);
    delete = (CardView) findViewById(R.id.passworddetaillayout_cardview_delete);
    passwordHistory = (RecyclerView) findViewById(R.id.passworddetaillayout_recyclerview_passwordhistory);
    Button button = (Button) findViewById(R.id.passworddetaillayout_button_generatepassword);

    setSupportActionBar(toolbar);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

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

    if(!button.requestFocus())
      button.requestFocus();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.create_user_menu, menu);
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
      newUsername = usernameTextWatcher.needUpdate() ? username.getText().toString() : null;
      newProgram = programTextWatcher.needUpdate() ? program.getText().toString() : null;
      newPassword = passwordTextWatcher.needUpdate() ? password.getText().toString() : null;
    } catch (Exception e) {
      // ignored
    }

    try {
      if (newPassword != null) {
        PasswordProvider.getInstance(this).editPassword(passwordId, newPassword);
      }

      if (newUsername != null || newProgram != null) {
        PasswordProvider.getInstance(this).editPassword(passwordId, newProgram, newUsername);
      }
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
