package com.typingsolutions.passwordmanager.activities;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.AddPasswordTextWatcher;
import core.Password;
import core.PasswordProvider;
import core.adapter.PasswordHistoryAdapter;

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
    private Password currentPassword;

    private boolean first = true;
    private View.OnLayoutChangeListener deleteLayoutChanged = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            int windowHeight = PasswordDetailActivity.this.getWindow().getDecorView().getHeight();

            if (first) {
                first = false;
                if (bottom > windowHeight) return;

                Rect passwordHistory = new Rect();
                Rect password = new Rect();

                passwordHistoryCard.getGlobalVisibleRect(passwordHistory);
                passwordCard.getGlobalVisibleRect(password);

                ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) passwordHistoryCard.getLayoutParams();

                int toolbarHeight = Build.VERSION.SDK_INT >= 21 ? toolbar.getMeasuredHeight() : 0;
                int additionalMargin = Build.VERSION.SDK_INT >= 21 ? (margin.topMargin * 2 + margin.bottomMargin) : 0;

                int newDeletePos = windowHeight - delete.getMeasuredHeight();
                int height = newDeletePos - additionalMargin - password.bottom - toolbarHeight;
                ViewGroup.LayoutParams params = passwordHistoryCard.getLayoutParams();
                params.height = height;
                passwordHistoryCard.setLayoutParams(params);
            }
        }
    };

    private PasswordHistoryAdapter.OnItemAddedCallback onItemAddedCallback = new PasswordHistoryAdapter.OnItemAddedCallback() {
        @Override
        public void onItemAdded(PasswordHistoryAdapter.ViewHolder viewHolder, int position) {
            ViewGroup.LayoutParams params = passwordHistory.getLayoutParams();
            params.height = params.height + 1;
            passwordHistory.setLayoutParams(params);
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

        passwordId = getIntent().getIntExtra(START_DETAIL_INDEX, -1);
        if (passwordId == -1) return;
        currentPassword = PasswordProvider.getInstance().getById(passwordId);

        layoutManager = new LinearLayoutManager(this);
        passwordHistoryAdapter = new PasswordHistoryAdapter(this, passwordId);
        passwordHistory.setLayoutManager(layoutManager);
        passwordHistory.setAdapter(passwordHistoryAdapter);
        passwordHistoryAdapter.setOnItemAddedCallback(onItemAddedCallback);

        String programString = currentPassword.getProgram();
        programTextWatcher = new AddPasswordTextWatcher(this, programString);
        program.setText(programString);
        program.addTextChangedListener(programTextWatcher);

        String usernameString = currentPassword.getUsername();
        usernameTextWatcher = new AddPasswordTextWatcher(this, usernameString);
        username.setText(usernameString);
        username.addTextChangedListener(usernameTextWatcher);

        String passwordString = currentPassword.getFirstItem().getValue();
        passwordTextWatcher = new AddPasswordTextWatcher(this, passwordString);
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

        String newUsername = usernameTextWatcher.needUpdate() ? username.getText().toString() : null;
        String newProgram = programTextWatcher.needUpdate() ? program.getText().toString() : null;
        String newPassword = passwordTextWatcher.needUpdate() ? password.getText().toString() : null;

        try {
            if (newPassword != null) {
                PasswordProvider.getInstance().addPasswordHistoryItem(passwordId, newPassword);
            }

            if (newUsername != null || newProgram != null) {
                PasswordProvider.getInstance().update(passwordId, newUsername, newProgram);
            }
        } catch (Exception e) {
            // ignored
        }

        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    public void switchMenuState(boolean state) {
        MenuItem item = toolbar.getMenu().getItem(0);
        item.setEnabled(state);
        if (state) {
            item.getIcon().setAlpha(255);
        } else {
            item.getIcon().setAlpha(64);
        }
    }
}
