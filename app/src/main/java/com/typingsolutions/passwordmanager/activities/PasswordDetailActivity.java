package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.R;
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
    private RecyclerView passwordHistory;
    private RecyclerView.LayoutManager layoutManager;
    private PasswordHistoryAdapter passwordHistoryAdapter;

    private int passwordId;
    private Password currentPassword;

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

        setSupportActionBar(toolbar);

        passwordId = getIntent().getIntExtra(START_DETAIL_INDEX, -1);
        if (passwordId == -1) return;
        currentPassword = PasswordProvider.getInstance().getById(passwordId);

        layoutManager = new LinearLayoutManager(this);
        passwordHistoryAdapter = new PasswordHistoryAdapter(this, passwordId);
        passwordHistory.setLayoutManager(layoutManager);
        passwordHistory.setAdapter(passwordHistoryAdapter);

        program.setText(currentPassword.getProgram());
        username.setText(currentPassword.getUsername());
        password.setText(currentPassword.getFirstItem().getValue());


    }

    @Override
    protected void onResume() {
        super.onResume();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_user_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
