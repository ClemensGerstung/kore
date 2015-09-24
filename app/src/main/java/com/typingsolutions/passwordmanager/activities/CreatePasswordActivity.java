package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.typingsolutions.passwordmanager.R;
import core.PasswordProvider;
import core.UserProvider;
import core.exceptions.PasswordProviderException;

public class CreatePasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText username;
    private EditText program;
    private EditText password;

    private TextWatcher switchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean result = username.getText().length() > 0
                    && program.getText().length() > 0
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

        View button = findViewById(R.id.passworddetail_appcompatbutton_delete);
        button.setVisibility(View.INVISIBLE);

        toolbar = (Toolbar)findViewById(R.id.passworddetail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        username = (EditText) findViewById(R.id.passworddetaillayout_edittext_username);
        program = (EditText) findViewById(R.id.passworddetaillayout_edittext_program);
        password = (EditText) findViewById(R.id.passworddetaillayout_edittext_password);

        username.addTextChangedListener(switchTextWatcher);
        program.addTextChangedListener(switchTextWatcher);
        password.addTextChangedListener(switchTextWatcher);
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

        switch(id) {
            case R.id.createusermenu_item_done:
                int userId = UserProvider.getInstance(this).getId();
                PasswordProvider provider = PasswordProvider.getInstance(this, userId);

                String program  = this.program.getText().toString();
                String username = this.username.getText().toString();
                String password = this.password.getText().toString();

                try {
                    provider.insertIntoDatabase(program, username, password);
                } catch (PasswordProviderException e) {
                    Snackbar.make(null, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }

                break;
        }

        return true;
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
