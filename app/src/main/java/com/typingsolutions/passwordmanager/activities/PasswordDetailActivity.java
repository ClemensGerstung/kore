package com.typingsolutions.passwordmanager.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import com.typingsolutions.passwordmanager.R;

public class PasswordDetailActivity extends Activity {

    private LinearLayout detailwrapper;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_detail_layout);

        detailwrapper = (LinearLayout) findViewById(R.id.passworddetaillayout_recyclerview_informationwrapper);
    }
}
