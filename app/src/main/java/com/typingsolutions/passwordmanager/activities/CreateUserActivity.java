package com.typingsolutions.passwordmanager.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.CreateUserCallback;
import core.UserProvider;
import core.Utils;
import core.exceptions.LoginException;
import core.exceptions.UserProviderException;

import java.security.NoSuchAlgorithmException;

public class CreateUserActivity extends AppCompatActivity {


    private Toolbar toolbar = null;
    private EditText usernameEditText = null;
    private EditText passwordEditText = null;
    private EditText repeatEditText = null;
    private SwitchCompat autoLoginSwitch = null;
    private View rootView;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean result = usernameEditText.getText().length() > 0
                    && passwordEditText.getText().length() > 0
                    && repeatEditText.getText().length() > 0;
            switchMenuState(result);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createuser_layout);

        toolbar = (Toolbar) findViewById(R.id.passworddetail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        usernameEditText = (EditText) findViewById(R.id.createuserlayout_edittext_username);
        usernameEditText.setText(getIntent().getStringExtra(CreateUserCallback.ADD_USER_CALLBACK_INTENT_NAME));
        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText = (EditText) findViewById(R.id.createuserlayout_edittext_password);
        passwordEditText.addTextChangedListener(textWatcher);
        repeatEditText = (EditText) findViewById(R.id.createuserlayout_edittext_repeatpassword);
        repeatEditText.addTextChangedListener(textWatcher);

        autoLoginSwitch = (SwitchCompat) findViewById(R.id.createuserlayout_switchcompat_autologin);

        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_user_menu, menu);
        switchMenuState(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.createusermenu_item_done) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

            // check password for equality
            String password = passwordEditText.getText().toString();
            String repeated = repeatEditText.getText().toString();
            if (!password.equals(repeated)) {
                Snackbar.make(rootView, "The passwords don't match", Snackbar.LENGTH_LONG).show();
                return false;
            }

            // check password for safety
            if (!Utils.isSafe(password)) {
                new AlertDialog.Builder(this)
                        .setTitle("Your password doesn't seem to be safe")
                        .setMessage("We recommend to use lower and upper letters, digits, some special characters and at least 8 characters. Do you want to keep it anyway?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                createUser();
                            }
                        })
                        .setNegativeButton("NOPE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                passwordEditText.requestFocus();
                                passwordEditText.setText("");
                                repeatEditText.setText("");

                            }
                        })
                        .create()
                        .show();
                return true;
            }

            // create user if everything is okay
            createUser();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createUser() {
        UserProvider userProvider = UserProvider.getInstance(this);
        try {
            userProvider.createUser(usernameEditText.getText().toString(), passwordEditText.getText().toString(), Utils.getSalt(), autoLoginSwitch.isChecked());
            Snackbar.make(rootView, "Created user " + usernameEditText.getText().toString(), Snackbar.LENGTH_LONG).show();
            onBackPressed();
        } catch (UserProviderException | RemoteException | NoSuchAlgorithmException | LoginException e) {
            Snackbar.make(rootView, e.getMessage(), Snackbar.LENGTH_LONG).show();
            usernameEditText.requestFocus();
            switchMenuState(false);
        }
    }

    private void switchMenuState(boolean state) {
        MenuItem item = toolbar.getMenu().getItem(0);
        item.setEnabled(state);
        if (state) {
            item.getIcon().setAlpha(255);
        } else {
            item.getIcon().setAlpha(64);
        }
    }
}
