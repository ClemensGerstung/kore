package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.GeneratePasswordCallback;
import core.data.PasswordProvider;

public class CreatePasswordActivity extends AppCompatActivity {


  private Toolbar toolbar;
  private EditText program;
  private EditText username;
  private EditText password;
  private Button button;

  private TextWatcher switchTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      boolean result = program.getText().length() > 0
          && password.getText().length() > 0;
      switchMenuState(result);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_detail_layout);

    toolbar = (Toolbar) findViewById(R.id.setuplayout_toolbar);
    program = (EditText) findViewById(R.id.passworddetaillayout_edittext_program);
    username = (EditText) findViewById(R.id.passworddetaillayout_edittext_username);
    password = (EditText) findViewById(R.id.passworddetaillayout_edittext_password);
    CardView delete = (CardView) findViewById(R.id.passworddetaillayout_cardview_delete);
    CardView passwordHistoryCard = (CardView) findViewById(R.id.passworddetaillayout_cardview_passwordhistory);
    button = (Button) findViewById(R.id.passworddetaillayout_button_generatepassword);

    setSupportActionBar(toolbar);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    username.addTextChangedListener(switchTextWatcher);
    program.addTextChangedListener(switchTextWatcher);
    password.addTextChangedListener(switchTextWatcher);
    button.setOnClickListener(new GeneratePasswordCallback(this, password));


    delete.setVisibility(View.GONE);
    passwordHistoryCard.setVisibility(View.GONE);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.create_user_menu, menu);
    boolean result = program.getText().length() > 0
        && password.getText().length() > 0;
    switchMenuState(result);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id != R.id.createusermenu_item_done) return false;

    String program = this.program.getText().toString();
    String username = this.username.getText().toString();
    String password = this.password.getText().toString();

    try {
      PasswordProvider.getInstance(this).addPassword(program, username, password);
    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
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

  private void switchMenuState(boolean state) {
    try {
      MenuItem item = toolbar.getMenu().getItem(0);
      item.setEnabled(state);
      item.getIcon().setAlpha(state ? 255 : 64);
    } catch (Exception e) {
      // ignored
    }
  }
}
