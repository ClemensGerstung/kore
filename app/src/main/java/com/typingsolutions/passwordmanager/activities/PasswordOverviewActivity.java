package com.typingsolutions.passwordmanager.activities;

import android.content.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.SimpleItemTouchHelperCallback;
import com.typingsolutions.passwordmanager.callbacks.click.AddPasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.OnOrderDialogShowCallback;
import com.typingsolutions.passwordmanager.utils.PasswordOverviewItemAnimator;
import core.DatabaseProvider;
import core.async.AsyncPasswordLoader;
import com.typingsolutions.passwordmanager.adapter.PasswordOverviewAdapter;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.PasswordProvider;

public class PasswordOverviewActivity extends AppCompatActivity {

  public static final String WRONGPASSWORD = "com.typingsolutions.passwordmanager.activitiesPasswordOverviewActivity.WRONGPASSWORD";

  private RecyclerView passwordRecyclerView;
  private Toolbar toolbar;
  private FloatingActionButton addPasswordFloatingActionButton;
  private TextView noPasswordsTextView;
  private MenuItem searchItem;
  private SwipeRefreshLayout swipeRefreshLayout;

  private boolean logout = true;

  private SearchView searchView;
  private PasswordOverviewAdapter passwordOverviewAdapter;
  private AsyncPasswordLoader passwordLoader;

  private RecyclerView.LayoutManager layoutManager;

  private final BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (!PasswordProvider.isLoggedIn())
        return;

      PasswordProvider.logoutComplete();

      Intent loginIntent = new Intent(PasswordOverviewActivity.this, LoginActivity.class);
      startActivity(loginIntent);

      PasswordOverviewActivity.this.finish();
    }
  };

  private PasswordProvider.PasswordActionListener passwordActionListener = new PasswordProvider.PasswordActionListener() {
    @Override
    public void onPasswordAdded(Password password) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (noPasswordsTextView.getVisibility() == View.VISIBLE)
            noPasswordsTextView.setVisibility(View.INVISIBLE);

          passwordOverviewAdapter.notifyDataSetChanged();
        }
      });
    }

    @Override
    public void onPasswordRemoved(Password password) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          passwordOverviewAdapter.notifyDataSetChanged();
        }
      });

      if (PasswordProvider.getInstance(PasswordOverviewActivity.this).size() > 0)
        return;

      if (noPasswordsTextView.getVisibility() == View.INVISIBLE)
        noPasswordsTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPasswordEdited(Password password, PasswordHistory history) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          passwordOverviewAdapter.notifyDataSetChanged();
        }
      });
    }

    @Override
    public void onOrder() {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          passwordOverviewAdapter.notifyDataSetChanged();
        }
      });
    }
  };
  private MenuItemCompat.OnActionExpandListener onSearchViewOpen = new MenuItemCompat.OnActionExpandListener() {
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
      passwordOverviewAdapter.resetFilter();
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_list_layout);

    // set action listener for passwordprovider
    PasswordProvider.getInstance(this).setPasswordActionListener(passwordActionListener);

    // get elements from XML-View
    passwordRecyclerView = (RecyclerView) findViewById(R.id.passwordlistlayout_listview_passwords);
    toolbar = (Toolbar) findViewById(R.id.passwordlistlayout_toolbar);
    addPasswordFloatingActionButton = (FloatingActionButton) findViewById(R.id.passwordlistlayout_floatingactionbutton_add);
    noPasswordsTextView = (TextView) findViewById(R.id.passwordlistlayout_textview_nopasswords);
    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.passwordlistlayout_swiperefreshlayout_wrapper);
    swipeRefreshLayout.setEnabled(false);
    swipeRefreshLayout.setColorSchemeColors(R.color.orange, R.color.green, R.color.blue);

    // set onClick-event to add new passwords
    addPasswordFloatingActionButton.setOnClickListener(new AddPasswordCallback(this));

    // ...
    setSupportActionBar(toolbar);

    // init and set adapter
    passwordOverviewAdapter = new PasswordOverviewAdapter(this);
    layoutManager = new LinearLayoutManager(this);
    passwordRecyclerView.setAdapter(passwordOverviewAdapter);
    passwordRecyclerView.setLayoutManager(layoutManager);

    PasswordOverviewItemAnimator animator = new PasswordOverviewItemAnimator(this);
    //passwordRecyclerView.setItemAnimator(animator);

    SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(this, passwordOverviewAdapter);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
    itemTouchHelper.attachToRecyclerView(passwordRecyclerView);

    // make secure
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


    // load passwords in background
    passwordLoader = new AsyncPasswordLoader(this);
    passwordLoader.execute();

    registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
  }

  @Override
  protected void onResume() {
    super.onResume();
    logout = true;
  }

  @Override
  protected void onStop() {
    Log.d(getClass().getSimpleName(), String.format("Logout: %s", logout));
    if(logout) {
      PasswordProvider.logoutComplete();
      DatabaseProvider.logout();
      finish();
    }
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(screenOffReceiver);
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    AlertDialog alertDialog = new AlertDialog.Builder(this)
        .setTitle("Logout")
        .setMessage("Are you sure to logout?")
        .setNegativeButton("Discard", null)
        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            logout();
          }
        })
        .create();
    // TODO: set onKeyListener for alertdialog on back pressed
    alertDialog.show();
  }

  private void logout() {
    PasswordProvider.logoutComplete();
    DatabaseProvider.logout();

    Intent intent = new Intent(PasswordOverviewActivity.this, LoginActivity.class);
    startActivity(intent);

    PasswordOverviewActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    PasswordOverviewActivity.super.onBackPressed();
    ActivityCompat.finishAfterTransition(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.passwordoverviewlayout_menu, menu);

    // init searchview
    searchItem = menu.findItem(R.id.passwordoverviewlayout_menuitem_search);
    searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setOnQueryTextListener(onQueryTextListener);
    MenuItemCompat.setOnActionExpandListener(searchItem, onSearchViewOpen);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case R.id.passwordoverviewlayout_menuitem_order:

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setView(R.layout.order_layout)
            .setNegativeButton("discard", null)
            .setPositiveButton("order", null)
            .create();

        alertDialog.setOnShowListener(new OnOrderDialogShowCallback(this));
        alertDialog.show();
        break;
      case R.id.passwordoverviewlayout_menuitem_logout:
        onBackPressed();
        break;
      case R.id.passwordoverviewlayout_menuitem_about:
        logout = false;
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        break;
      case R.id.passwordoverviewlayout_menuitem_backup:
        logout = false;
        intent = new Intent(this, BackupRestoreActivity.class);
        startActivity(intent);
        break;
    }

    return true;
  }

  public void makeSnackBar(String text) {
    Snackbar.make(addPasswordFloatingActionButton, text, Snackbar.LENGTH_LONG).show();
  }

  public void setRefreshing(final boolean refreshing) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        swipeRefreshLayout.setRefreshing(refreshing);
      }
    });
  }

  public void doNotLogout() {
    logout = false;
  }
}
