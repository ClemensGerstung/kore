package com.typingsolutions.passwordmanager.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.typingsolutions.passwordmanager.R;

public class PasswordDetailActivity extends Activity {

    public static final String START_DETAIL_INDEX = "com.typingsolutions.passwordmanager.activities.PasswordDetailActivity.START_DETAIL_INDEX";

    private EditText program;
    private EditText username;
    private EditText password;
    private Button delete;
    private RecyclerView passwordHistory;
    private RecyclerView.LayoutManager layoutManager;


    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_detail_layout);




        position = getIntent().getIntExtra(START_DETAIL_INDEX, -1);
        if(position == -1) return;


    }
}
