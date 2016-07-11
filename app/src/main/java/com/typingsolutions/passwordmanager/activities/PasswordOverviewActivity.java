package com.typingsolutions.passwordmanager.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.*;
import com.typingsolutions.passwordmanager.adapter.PasswordOverviewAdapter;
import com.typingsolutions.passwordmanager.callbacks.AddPasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.OnOrderDialogShowCallback;
import com.typingsolutions.passwordmanager.callbacks.SimpleItemTouchHelperCallback;

public class PasswordOverviewActivity extends BaseDatabaseActivity {

  private RecyclerView mRecyclerViewAsPasswordsList;
  private Toolbar mToolbarAsActionBar;
  private FloatingActionButton mFloatingActionButtonAsAddPassword;
  private TextView mTextViewAsNoPasswordsYet;
  private MenuItem mMenuItemAsSearchViewWrapper;
  private SwipeRefreshLayout mSwipeRefreshLayoutAsLoadingIndication;
  private SearchView mSearchViewAsSearchView;
  private ImageView mImageViewAsBackground;

  private PasswordOverviewAdapter passwordOverviewAdapter;

  private RecyclerView.LayoutManager layoutManager;

  static {
    logout = false;
  }

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
    //Log.d(getClass().getSimpleName(), "onCreate BEGIN");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_list_layout);
    //getIntent().getExtras()

    // set action listener for passwordprovider
    //PasswordProvider.getInstance(this).setPasswordActionListener(passwordActionListener);

    // get elements from XML-View
    mRecyclerViewAsPasswordsList = findCastedViewById(R.id.passwordlistlayout_listview_passwords);
    mToolbarAsActionBar = findCastedViewById(R.id.passwordlistlayout_toolbar);
    mFloatingActionButtonAsAddPassword = findCastedViewById(R.id.passwordlistlayout_floatingactionbutton_add);
    mTextViewAsNoPasswordsYet = findCastedViewById(R.id.passwordlistlayout_textview_nopasswords);
    mSwipeRefreshLayoutAsLoadingIndication = findCastedViewById(R.id.passwordlistlayout_swiperefreshlayout_wrapper);
    mImageViewAsBackground = findCastedViewById(R.id.passwordlistlayout_imageview_background);


    mSwipeRefreshLayoutAsLoadingIndication.setEnabled(false);
    mSwipeRefreshLayoutAsLoadingIndication.setColorSchemeColors(R.color.orange, R.color.green, R.color.blue);

    // set onClick-event to add new passwords
    mFloatingActionButtonAsAddPassword.setOnClickListener(new AddPasswordCallback(this));

    // ...
    setSupportActionBar(mToolbarAsActionBar);

    // init and set adapter
    passwordOverviewAdapter = new PasswordOverviewAdapter(this);
    layoutManager = new LinearLayoutManager(this);
    mRecyclerViewAsPasswordsList.setAdapter(passwordOverviewAdapter);
    mRecyclerViewAsPasswordsList.setLayoutManager(layoutManager);

    //PasswordOverviewItemAnimator animator = new PasswordOverviewItemAnimator(this);
    //mRecyclerViewAsPasswordsList.setItemAnimator(animator);

    SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(this, passwordOverviewAdapter);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
    itemTouchHelper.attachToRecyclerView(mRecyclerViewAsPasswordsList);

    // make secure
    if (!debug)
      this.setSecurityFlags();

    // load passwords in background
    //LoadPasswordsTask loadPasswords = new LoadPasswordsTask();

    mImageViewAsBackground.setImageBitmap(getBitmap(this, R.mipmap.lock_large, 1, 0.75f));
    mFloatingActionButtonAsAddPassword.setImageBitmap(getBitmap(this, R.mipmap.add, 1, 1));
  }

  @Override
  public void onBackPressed() {
    // TODO: set onKeyListener for alertdialog on back pressed
    AlertBuilder.create(this)
        .setTitle("Logout")
        .setMessage("Are you sure to logout?")
        .setNegativeButton("Discard")
        .setPositiveButton("Logout")
        .setCallback(new BaseDialogCallback<BaseActivity>(this) {
          @Override
          public void OnPositiveButtonPressed(DialogInterface dialog) {
            logout();
          }
        })
        .show();
  }

  private void logout() {
    //PasswordProvider.logoutComplete();
    //DatabaseProvider.logout();
    logout = false;

    super.onBackPressed();

    startActivity(LoginActivity.class, true, R.anim.slide_in_left, R.anim.slide_out_right);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.passwordoverviewlayout_menu, menu);

    // init searchview
    /*mMenuItemAsSearchViewWrapper = menu.findItem(R.id.passwordoverviewlayout_menuitem_search);
    mSearchViewAsSearchView = (SearchView) MenuItemCompat.getActionView(mMenuItemAsSearchViewWrapper);
    mSearchViewAsSearchView.setOnQueryTextListener(onQueryTextListener);
    MenuItemCompat.setOnActionExpandListener(mMenuItemAsSearchViewWrapper, onSearchViewOpen);*/

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
        startActivity(AboutActivity.class);
        break;
      case R.id.passwordoverviewlayout_menuitem_backup:
        logout = false;
        startActivity(BackupRestoreActivity.class);
        break;
    }

    return true;
  }

  public void setRefreshing(final boolean refreshing) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mSwipeRefreshLayoutAsLoadingIndication.setRefreshing(refreshing);
      }
    });
  }

  @Override
  protected View getSnackbarRelatedView() {
    return this.mFloatingActionButtonAsAddPassword;
  }
}
