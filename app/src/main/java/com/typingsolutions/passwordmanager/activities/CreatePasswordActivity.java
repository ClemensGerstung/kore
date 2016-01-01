package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.click.GeneratePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.click.ToolbarNavigationCallback;
import core.data.PasswordProvider;

public class CreatePasswordActivity extends AppCompatActivity {

  private static final int[] COLORS =
      {
          R.color.createpassword_red,
          R.color.createpassword_yellow,
          R.color.createpassword_green,
          R.color.createpassword_purple,
          R.color.createpassword_lime,
          R.color.createpassword_orange,
          R.color.createpassword_grey
      };

  private Toolbar toolbar;
  private AppBarLayout appBarLayout;
  private EditText program;
  private NestedScrollView nestedScrollView;
  private EditText username;
  private EditText password;
  private Button generatePassword;

  private final TextWatcher switchTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      boolean result = program.length() > 0
          && password.length() > 0;
      switchMenuState(result);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
  };

  private final NestedScrollView.OnScrollChangeListener scrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
      float elevation=0;
      if(scrollY > 0) {
        elevation = CreatePasswordActivity.this.getResources().getDimension(R.dimen.dimen_sm);
      } else if(scrollY <= 0) {
        elevation = CreatePasswordActivity.this.getResources().getDimension(R.dimen.zero);
      }

      ViewCompat.setElevation(appBarLayout, elevation);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_password_layout);

    appBarLayout = (AppBarLayout) findViewById(R.id.createpasswordlayout_appbar_wrapper);
    toolbar = (Toolbar) appBarLayout.findViewById(R.id.createpasswordlayout_toolbar_actionbar);
    program = (EditText) appBarLayout.findViewById(R.id.createpasswordlayout_edittext_program);
    nestedScrollView = (NestedScrollView) findViewById(R.id.createpasswordlayout_nestedscrollview_wrapper);
    username = (EditText) nestedScrollView.findViewById(R.id.createpasswordlayout_edittext_username);
    password = (EditText) nestedScrollView.findViewById(R.id.createpasswordlayout_edittext_password);
    generatePassword = (Button) nestedScrollView.findViewById(R.id.createpasswordlayout_button_generatepassword);

    setSupportActionBar(toolbar);
    toolbar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));

    program.addTextChangedListener(switchTextWatcher);
    password.addTextChangedListener(switchTextWatcher);

    generatePassword.setOnClickListener(new GeneratePasswordCallback(this, password));

    nestedScrollView.setOnScrollChangeListener(scrollChangeListener);
    nestedScrollView.requestFocus();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.create_password_menu, menu);
    switchMenuState(false);
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
