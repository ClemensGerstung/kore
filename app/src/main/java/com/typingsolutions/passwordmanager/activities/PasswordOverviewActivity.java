package com.typingsolutions.passwordmanager.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.*;
import com.typingsolutions.passwordmanager.adapter.PasswordOverviewAdapter;
import com.typingsolutions.passwordmanager.async.LoadPasswordsTask;
import com.typingsolutions.passwordmanager.callbacks.*;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;

public class PasswordOverviewActivity extends BaseDatabaseActivity
    implements IListChangedListener<IContainer> {

  private RecyclerView mRecyclerViewAsPasswordsList;
  private Toolbar mToolbarAsActionBar;
  private FloatingActionButton mFloatingActionButtonAsAddPassword;
  private TextView mTextViewAsNoPasswordsYet;
  private MenuItem mMenuItemAsSearchViewWrapper;
  private SwipeRefreshLayout mSwipeRefreshLayoutAsLoadingIndication;
  private SearchView mSearchViewAsSearchView;
  private ImageView mImageViewAsBackground;

  private PasswordOverviewAdapter mPasswordOverviewAdapter;
  private LogoutDialogCallback mLogoutDialogCallback = new LogoutDialogCallback(this);
  private OrderDialogShowCallback mOrderDialogCallback = new OrderDialogShowCallback(this);
  private SearchViewExpandCallback searchViewExpandCallback = new SearchViewExpandCallback(this);
  private SearchViewQueryCallback onQueryTextListener = new SearchViewQueryCallback(this);

  private RecyclerView.LayoutManager layoutManager;

  private boolean mSafe = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_list_layout);
    logout = true;
    mSafe = getIntent().getBooleanExtra(LoginActivity.SAFE_LOGIN, true);

    // get elements from XML-View
    mRecyclerViewAsPasswordsList = findCastedViewById(R.id.passwordlistlayout_listview_passwords);
    mToolbarAsActionBar = findCastedViewById(R.id.passwordlistlayout_toolbar);
    mFloatingActionButtonAsAddPassword = findCastedViewById(R.id.passwordlistlayout_floatingactionbutton_add);
    mTextViewAsNoPasswordsYet = findCastedViewById(R.id.passwordlistlayout_textview_nopasswords);
    mSwipeRefreshLayoutAsLoadingIndication = findCastedViewById(R.id.passwordlistlayout_swiperefreshlayout_wrapper);
    mImageViewAsBackground = findCastedViewById(R.id.passwordlistlayout_imageview_background);


    mSwipeRefreshLayoutAsLoadingIndication.setEnabled(false);
    mSwipeRefreshLayoutAsLoadingIndication.setColorSchemeColors(
        ContextCompat.getColor(this, R.color.orange),
        ContextCompat.getColor(this, R.color.green),
        ContextCompat.getColor(this, R.color.blue));

    // set onClick-event to add new passwords
    mFloatingActionButtonAsAddPassword.setOnClickListener(new AddPasswordCallback(this));

    // ...
    setSupportActionBar(mToolbarAsActionBar);

    // init and set adapter
    mPasswordOverviewAdapter = new PasswordOverviewAdapter(this);
    mRecyclerViewAsPasswordsList.setAdapter(mPasswordOverviewAdapter);

    layoutManager = new LinearLayoutManager(this);

    mRecyclerViewAsPasswordsList.setLayoutManager(layoutManager);

    //PasswordOverviewItemAnimator animator = new PasswordOverviewItemAnimator(this);
    //mRecyclerViewAsPasswordsList.setItemAnimator(animator);

    SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(this, mPasswordOverviewAdapter);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
    itemTouchHelper.attachToRecyclerView(mRecyclerViewAsPasswordsList);

    registerListChangedListener(this);

    // load passwords in background
    LoadPasswordsTask loadPasswords = new LoadPasswordsTask();
    loadPasswords.registerCallback(new BaseAsyncTask.IExecutionCallback<PasswordContainer>() {
      @Override
      public void executed(final PasswordContainer result) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            addContainerItem(result);
          }
        });
      }

      @Override
      public void failed(int code, String message) {

      }
    });
    loadPasswords.execute();

    //mImageViewAsBackground.setImageBitmap(getBitmap(this, R.mipmap.lock_large, 1, 0.75f));
    mFloatingActionButtonAsAddPassword.setImageBitmap(getBitmap(this, R.mipmap.add, 1, 1));
  }

  @Override
  public void onBackPressed() {
    AlertBuilder builder = AlertBuilder.create(this)
        .setTitle("Logout")
        .setMessage("Are you sure to logout?")
        .setNegativeButton("Cancel")
        .setPositiveButton("Logout")
        .setCallback(mLogoutDialogCallback);

    AlertDialog dialog = builder.getDialog();
//    TODO: fix somehow
//    dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//      @Override
//      public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//          dialog.dismiss();
//          startActivity(LoginActivity.class, false, R.anim.slide_in_left, R.anim.slide_out_right);
//        }
//        return true;
//      }
//    });

    builder.show();
  }

  public void logout() {
    //PasswordProvider.logoutComplete();
    //DatabaseProvider.logout();
    logout = true;

    super.onBackPressed();

    startActivity(LoginActivity.class, false, R.anim.slide_in_left, R.anim.slide_out_right);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.passwordoverviewlayout_menu, menu);

    // init searchview
    mMenuItemAsSearchViewWrapper = menu.findItem(R.id.passwordoverviewlayout_menuitem_search);
    mSearchViewAsSearchView = (SearchView) MenuItemCompat.getActionView(mMenuItemAsSearchViewWrapper);
    mSearchViewAsSearchView.setOnQueryTextListener(onQueryTextListener);
    MenuItemCompat.setOnActionExpandListener(mMenuItemAsSearchViewWrapper, searchViewExpandCallback);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case R.id.passwordoverviewlayout_menuitem_order:
        AlertBuilder.create(this)
            .setView(R.layout.order_layout)
            .setPositiveButton("order")
            .setNegativeButton("cancel")
            .setCallback(mOrderDialogCallback)
            .show();
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

  public boolean isSafe() {
    return mSafe;
  }

  @Override
  protected View getSnackbarRelatedView() {
    return mFloatingActionButtonAsAddPassword;
  }

  @Override
  protected void onActivityChange() {
    if (logout) {
//      mRecyclerViewAsPasswordsList.removeAllViews();
      clearContainerItems();
      clearChangeListener();
      mPasswordOverviewAdapter.notifyDataSetChanged();
      mRecyclerViewAsPasswordsList.destroyDrawingCache();
    }
  }

  @Override
  public void onItemAdded(int index, IContainer item) {
    mPasswordOverviewAdapter.notifyDataSetChanged();

    if (mTextViewAsNoPasswordsYet != null && mTextViewAsNoPasswordsYet.getVisibility() != View.GONE) {
      mTextViewAsNoPasswordsYet.setVisibility(View.GONE);
    }
  }

  @Override
  public void onItemRemoved(int index, final IContainer item) {
    mPasswordOverviewAdapter.notifyItemRemoved(index);
    if (mTextViewAsNoPasswordsYet.getVisibility() == View.GONE && containerCount() == 0) {
      mTextViewAsNoPasswordsYet.setVisibility(View.VISIBLE);
    }

    ((PasswordContainer) item).delete();
    makeSnackbar("Password deleted");
  }

  @Override
  public void onItemChanged(int index, IContainer oldItem, IContainer newItem) {
    mPasswordOverviewAdapter.notifyItemChanged(index);
  }

  public void search(String query) {
    mPasswordOverviewAdapter.search(query);
  }

  public void order(PasswordOverviewAdapter.OrderOptions options) {
    mPasswordOverviewAdapter.order(options);
  }
}
