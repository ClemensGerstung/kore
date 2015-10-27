package com.typingsolutions.passwordmanager.activities;

import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.AddPasswordCallback;
import com.typingsolutions.passwordmanager.receiver.WrongPasswordReceiver;
import core.*;
import core.adapter.PasswordOverviewAdapter;
import core.data.Password;
import core.data.UserProvider;

public class PasswordOverviewActivity extends AppCompatActivity {

    public static final String WRONGPASSWORD = "com.typingsolutions.passwordmanager.activitiesPasswordOverviewActivity.WRONGPASSWORD";

    private RecyclerView passwordRecyclerView;
    private Toolbar toolbar;
    private FloatingActionButton addPasswordFloatingActionButton;
    private TextView noPasswordsTextView;
    private SearchView searchView;

    private PasswordOverviewAdapter passwordOverviewAdapter;
    private AsyncPasswordLoader passwordLoader;
    private RecyclerView.LayoutManager layoutManager;

    private WrongPasswordReceiver wrongPasswordReceiver;

    private int userId;

//    private AsyncPasswordLoader.ItemAddedListener itemAddCallback = new AsyncPasswordLoader.ItemAddedListener() {
//        @Override
//        public void itemAdded(Password password) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (noPasswordsTextView.getVisibility() == View.VISIBLE) {
//                        Log.i(getClass().getSimpleName(), "make invisible");
//                        noPasswordsTextView.setVisibility(View.INVISIBLE);
//                    }
//
//                    passwordOverviewAdapter.notifyDataSetChanged();
//                }
//            });
//        }
//    };


    private SearchView.OnCloseListener mOnCloseListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            passwordOverviewAdapter.resetFilter();
            // do not override default behaviour
            return false;
        }
    };

    private MenuItemCompat.OnActionExpandListener onSearchViewOpen = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {


            return true;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {

            return true;
        }
    };

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            passwordOverviewAdapter.filter(newText);
            return false;
        }
    };
    private DialogInterface.OnClickListener orderItemClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
//            PasswordProvider.getInstance().order(which);
            passwordOverviewAdapter.notifyDataSetChanged();
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
        userId = userProvider.getId();

        // init and set adapter
        passwordOverviewAdapter = new PasswordOverviewAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        passwordRecyclerView.setAdapter(passwordOverviewAdapter);
        passwordRecyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // load passwords in background
        passwordLoader = new AsyncPasswordLoader(this);
        passwordLoader.execute();

        wrongPasswordReceiver = new WrongPasswordReceiver(this);
        IntentFilter filter = new IntentFilter(WRONGPASSWORD);
        getApplicationContext().registerReceiver(wrongPasswordReceiver, filter);
    }

    @Override
    protected void onPause() {
        getApplicationContext().unregisterReceiver(wrongPasswordReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
//        PasswordProvider.logout();
        UserProvider.logout();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.password_list_menu, menu);

        // init searchview
        MenuItem searchItem = menu.findItem(R.id.passwordlistmenu_item_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        MenuItemCompat.setOnActionExpandListener(searchItem, onSearchViewOpen);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.passwordlistmenu_item_order:
                String[] orderOptions = getResources().getStringArray(R.array.order_options);

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Order passwords by...")
                        .setItems(orderOptions, orderItemClickListener)
                        .create();

                alertDialog.show();
                break;
        }

        return true;
    }

    public void makeSnackBar() {
        Snackbar.make(addPasswordFloatingActionButton, "Your passwords do not match", Snackbar.LENGTH_LONG).show();
    }
}
