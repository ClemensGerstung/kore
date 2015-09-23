package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ListViewCompat;
import com.typingsolutions.passwordmanager.R;

public class PasswordOverviewActivity extends AppCompatActivity {

    private ListViewCompat listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_list_layout);

        listView = (ListViewCompat) findViewById(R.id.passwordlistlayout_listview_passwords);
    }
}
