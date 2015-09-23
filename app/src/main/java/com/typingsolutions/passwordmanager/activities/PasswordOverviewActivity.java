package com.typingsolutions.passwordmanager.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.AddPasswordCallback;

public class PasswordOverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListViewCompat listView;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_list_layout);

        listView = (ListViewCompat) findViewById(R.id.passwordlistlayout_listview_passwords);
        toolbar = (Toolbar) findViewById(R.id.passworddetail_toolbar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.passwordlistlayout_floatingactionbutton_add);
        floatingActionButton.setOnClickListener(new AddPasswordCallback(this));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
