package com.typingsolutions.passwordmanager.callbacks;

import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import com.typingsolutions.passwordmanager.BaseCallback;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;

public class SearchViewExpandCallback extends BaseCallback<PasswordOverviewActivity>
    implements MenuItemCompat.OnActionExpandListener {

  public SearchViewExpandCallback(PasswordOverviewActivity activity) {
    super(activity);
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public boolean onMenuItemActionExpand(MenuItem item) {

    return true;
  }

  @Override
  public boolean onMenuItemActionCollapse(MenuItem item) {
    return true;
  }
}
