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
import android.widget.EditText;
import com.typingsolutions.passwordmanager.LinearLayoutManager;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.AddPasswordTextWatcher;
import core.adapter.PasswordHistoryAdapter;
import core.data.Password;
import core.data.UserProvider;

public class PasswordDetailActivity extends AppCompatActivity {

    public static final String START_DETAIL_INDEX = "com.typingsolutions.passwordmanager.activities.PasswordDetailActivity.START_DETAIL_INDEX";

    private Toolbar toolbar;
    private EditText program;
    private EditText username;
    private EditText password;
    private CardView delete;
    private CardView passwordHistoryCard;
    private CardView passwordCard;
    private RecyclerView passwordHistory;

    private RecyclerView.LayoutManager layoutManager;
    private PasswordHistoryAdapter passwordHistoryAdapter;

    private AddPasswordTextWatcher usernameTextWatcher;
    private AddPasswordTextWatcher programTextWatcher;
    private AddPasswordTextWatcher passwordTextWatcher;

    private int passwordId;

    private boolean first = true;

    private View.OnLayoutChangeListener deleteLayoutChanged = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            int windowHeight = PasswordDetailActivity.this.getWindow().getDecorView().getHeight();

            if (first) {
                first = false;
                if (bottom > windowHeight) return;

                Rect phr = new Rect();
                Rect password = new Rect();

                passwordHistoryCard.getGlobalVisibleRect(phr);
                passwordCard.getGlobalVisibleRect(password);

                ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) passwordHistoryCard.getLayoutParams();

                int toolbarHeight = Build.VERSION.SDK_INT >= 21 ? toolbar.getMeasuredHeight() : 0;
                int additionalMargin = Build.VERSION.SDK_INT >= 21 ? (margin.topMargin * 2 + margin.bottomMargin) : 0;

                int newDeletePos = windowHeight - delete.getMeasuredHeight();
                int historyCardHeight = newDeletePos - additionalMargin - password.bottom - toolbarHeight;
                Log.i(getClass().getSimpleName(), String.format("Height: %s", historyCardHeight));

                ViewGroup.LayoutParams params = passwordHistoryCard.getLayoutParams();
                if (historyCardHeight > passwordHistoryCard.getMeasuredHeight()) {
                    params.height = historyCardHeight;
                }
                passwordHistoryCard.setLayoutParams(params);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_detail_layout);

        toolbar = (Toolbar) findViewById(R.id.passworddetail_toolbar);
        program = (EditText) findViewById(R.id.passworddetaillayout_edittext_program);
        username = (EditText) findViewById(R.id.passworddetaillayout_edittext_username);
        password = (EditText) findViewById(R.id.passworddetaillayout_edittext_password);
        delete = (CardView) findViewById(R.id.passworddetaillayout_cardview_delete);
        passwordHistory = (RecyclerView) findViewById(R.id.passworddetaillayout_recyclerview_passwordhistory);
        passwordHistoryCard = (CardView) findViewById(R.id.passworddetaillayout_cardview_passwordhistory);
        passwordCard = (CardView) findViewById(R.id.passworddetaillayout_cardview_password);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        UserProvider userProvider = UserProvider.getInstance(this);

        passwordId = getIntent().getIntExtra(START_DETAIL_INDEX, -1);
        if (passwordId == -1) return;
        Password currentPassword = userProvider.getPasswordById(passwordId);

        layoutManager = new LinearLayoutManager(this);
        passwordHistoryAdapter = new PasswordHistoryAdapter(this, passwordId);
        passwordHistory.setLayoutManager(layoutManager);
        passwordHistory.setAdapter(passwordHistoryAdapter);


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

        delete.addOnLayoutChangeListener(deleteLayoutChanged);
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
            e.printStackTrace();
        }

        try {
            if (newPassword != null) {
                UserProvider.getInstance(this).editPassword(passwordId, newPassword);
            }

            if (newUsername != null || newProgram != null) {
                UserProvider.getInstance(this).editPassword(passwordId, newUsername, newProgram);
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }

        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    public void switchMenuState(boolean state) {
        MenuItem item = toolbar.getMenu().getItem(0);
        item.setEnabled(state);
        item.getIcon().setAlpha(state ? 255 : 64);
    }
}
