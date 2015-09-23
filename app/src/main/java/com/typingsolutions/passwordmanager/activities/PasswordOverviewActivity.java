package com.typingsolutions.passwordmanager.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.AddPasswordCallback;
import core.AsyncPasswordLoader;
import core.DatabaseProvider;
import core.Password;
import core.UserProvider;
import core.adapter.PasswordOverviewAdapter;

public class PasswordOverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Password> {

    static final int PASSWORD_LOADER_ID = 0xCCCC;

    private ListViewCompat passwordListViewCompat;
    private Toolbar toolbar;
    private FloatingActionButton addPasswordFloatingActionButton;
    private TextView noPasswordsTextView;

    private PasswordOverviewAdapter passwordOverviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_list_layout);

        // get elements from XML-View
        passwordListViewCompat = (ListViewCompat) findViewById(R.id.passwordlistlayout_listview_passwords);
        toolbar = (Toolbar) findViewById(R.id.passwordlistlayout_toolbar);
        addPasswordFloatingActionButton = (FloatingActionButton) findViewById(R.id.passwordlistlayout_floatingactionbutton_add);
        noPasswordsTextView = (TextView) findViewById(R.id.passwordlistlayout_textview_nopasswords);

        // set onClick-event to add new passwords
        addPasswordFloatingActionButton.setOnClickListener(new AddPasswordCallback(this));

        // ...
        setSupportActionBar(toolbar);

        // init and set adapter
        passwordOverviewAdapter = new PasswordOverviewAdapter(this);
        passwordListViewCompat.setAdapter(passwordOverviewAdapter);

        // init background loader for passwords
        getLoaderManager().initLoader(PASSWORD_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().getLoader(PASSWORD_LOADER_ID).forceLoad();
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

    @Override
    public Loader<Password> onCreateLoader(int id, Bundle bundle) {
        Loader<Password> loader = null;

        switch (id) {
            case PASSWORD_LOADER_ID:
                String userId = Integer.toString(UserProvider.getInstance(this).getId());
                loader = new AsyncPasswordLoader(this, DatabaseProvider.GET_ALL_PASSWORDS_BY_USER_ID, userId);
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Password> loader, Password password) {
        int id = loader.getId();
        switch (id) {
            case PASSWORD_LOADER_ID:

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Password> loader) {
        int id = loader.getId();
        switch (id) {
            case PASSWORD_LOADER_ID:

                break;
        }
    }


}
