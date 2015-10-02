package com.typingsolutions.passwordmanager.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.AddPasswordCallback;
import core.*;
import core.adapter.PasswordOverviewAdapter;

public class PasswordOverviewActivity extends AppCompatActivity {


    private RecyclerView passwordRecyclerView;
    private Toolbar toolbar;
    private FloatingActionButton addPasswordFloatingActionButton;
    private TextView noPasswordsTextView;

    private PasswordOverviewAdapter passwordOverviewAdapter;
    private AsyncPasswordLoader passwordLoader;
    private RecyclerView.LayoutManager layoutManager;

    private AsyncPasswordLoader.ItemAddCallback itemAddCallback = new AsyncPasswordLoader.ItemAddCallback() {
        @Override
        public void itemAdded(Password password) {
            int userId = UserProvider.getInstance(PasswordOverviewActivity.this).getId();
            PasswordProvider provider = PasswordProvider.getInstance(PasswordOverviewActivity.this, userId);

            if (!provider.contains(password)) {
                provider.add(password);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_list_layout);

        // get elements from XML-View
        passwordRecyclerView = (RecyclerView) findViewById(R.id.passwordlistlayout_listview_passwords);
        toolbar = (Toolbar) findViewById(R.id.passwordlistlayout_toolbar);
        addPasswordFloatingActionButton = (FloatingActionButton) findViewById(R.id.passwordlistlayout_floatingactionbutton_add);
        noPasswordsTextView = (TextView) findViewById(R.id.passwordlistlayout_textview_nopasswords);



        // set onClick-event to add new passwords
        addPasswordFloatingActionButton.setOnClickListener(new AddPasswordCallback(this));

        // ...
        setSupportActionBar(toolbar);

        // get userId
        UserProvider userProvider = UserProvider.getInstance(this);
        int userId = userProvider.getId();

        // init and set adapter
        passwordOverviewAdapter = new PasswordOverviewAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        passwordRecyclerView.setAdapter(passwordOverviewAdapter);
        passwordRecyclerView.setLayoutManager(layoutManager);

        // init passwordProvider
        PasswordProvider provider = PasswordProvider.getInstance(PasswordOverviewActivity.this, userId);
        provider.setOnPasswordAddedToDatabase(new PasswordProvider.OnPasswordAddedToDatabase() {
            @Override
            public void onPasswordAdded(int passwordId, int historyId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (noPasswordsTextView.getVisibility() == View.VISIBLE) {
                            noPasswordsTextView.setVisibility(View.INVISIBLE);
                        }

                        passwordOverviewAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

        // load passwords in background
        passwordLoader = new AsyncPasswordLoader(this, DatabaseProvider.GET_ALL_PASSWORDS_BY_USER_ID, Integer.toHexString(userId));
        passwordLoader.setItemAddCallback(itemAddCallback);
        passwordLoader.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.password_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }


}
